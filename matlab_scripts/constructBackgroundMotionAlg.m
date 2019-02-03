%this section does the actual background estimate
N_ITERATIONS_BCP = N_ITERATIONS;


% ---------------------DRAFT BACKGROUND ESTIMATION-----------------------
disp('Draft background estimation');
% calculate the first, draft background estimate as the "leftover" mask by
% calling moveMaskCreate with "soft" parameters, which means keeping really
% large margins all around the subject
N_ITERATIONS = 1;
proj_moveMaskCreate;
save_mask_full = zeros([height, width, 3],'logical');
save_mask_full(:,:,1) = save_mask(:,:,1);
save_mask_full(:,:,2) = save_mask_full(:,:,1);
save_mask_full(:,:,3) = save_mask_full(:,:,1);
background_candidate = uint8(~save_mask_full).*frames(:,:,:,1);
background_candidate_mask = ~save_mask_full;
clear('save_mask_full');


% -----------------------HIGH-LEVEL MASK CREATION-----------------------
disp('High-level mask creation');
% try to get much sharper contours of the subject, calling the algorithm
% with smaller and smaller  blocks 
N_ITERATIONS = N_ITERATIONS_BCP;
proj_moveMaskCreate;


% -----------------------HIGH-LEVEL MASK FILTERING-----------------------
% smoothens out the mask for less noisy contours, filtering it with a
% multivariate gaussian filter. Normalize it too.
disp('high-level mask filtering');
filtered = imgaussfilt3(double(save_mask), FILTER_SIGMA);
maxfilt = max(filtered, [], [1,2]);
for (f = 1:nframes)
    filtered(:,:,f) = filtered(:,:,f)/maxfilt(f);
end
lala =  filtered>FILTER_THRESHOLD;

% clip the mask as the beginning and end of it are pretty ugly, as a result
% of the convolution with the gaussian filter
clip_fr_end = floor(0.95*nframes);
clip_fr_beg = floor(0.05*nframes);
save_mask(:,:,clip_fr_beg:clip_fr_end) = lala(:,:,clip_fr_beg:clip_fr_end);
save_mask(:,:,1:(clip_fr_beg-1)) = ones([height, width, clip_fr_beg-1],'logical');
save_mask(:,:,(clip_fr_end+1):nframes) = ones([height, width, nframes-clip_fr_end+1-1],'logical');
FILTER_ON = 1;
i = 5;
clear('lala'); clear('filtered');


% -----------------BACKGROUND CANDIDATE CREATION----------------------
disp('Background candidate contruction');
bg_mask = ~save_mask;

% get a starting point for the fill we'll use
starting_point = [height, width];
while (background_candidate_mask(starting_point(1), starting_point(2),1) ~= 1)
    starting_point = floor([1+rand*height, 1+rand*width]);
end

% prealloccation
to_add_mask = zeros(height, width, 3);
to_add_mask = zeros(height, width);
filled_mask = zeros(height, width, 3);
% this loop tries to get more and more prediction of the background
for (fr = 2:nframes)
    
    % for every frame, calculate the difference between the actual,
    % best-estimate mask (our "sure it's background" estimate portion 
    % of the frame)
    mask_difference = bg_mask(:,:,fr) & ~background_candidate_mask;
    
    % let's understand which portion of this frame's background mask
    % is connected with the "sure it's background" portion
      
    % to get the connected part of it, let's use a bucket fill using a
    % starting point inside the "sure it's background" portion
    filled_mask(:,:,1) = (imfill(save_mask(:,:,fr) , starting_point)) & bg_mask(:,:,fr);
    filled_mask(:,:,2) = filled_mask(:,:,1);
    filled_mask(:,:,3) = filled_mask(:,:,1);
    % selezioniamo solo la parte nuova... logica come sopra
    to_add_mask = filled_mask & mask_difference;
    
    background_candidate_mask = background_candidate_mask | to_add_mask;
    background_candidate = background_candidate + (frames(:,:,:,fr).*uint8(to_add_mask));
    
    % problema: e se una maschera save non ? giusta e mangia un poco il
    % soggetto? devo avere dei bei parametri!
end

%now the UNCALCULATED part of the background_candidate is black, let's set
%it to green to make sure there's no clash from a black subject! (of course 
% a green subject would clash...)
background_candidate = background_candidate + ...
            uint8(~background_candidate_mask).*background_green_screen;