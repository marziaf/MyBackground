% this script prepares the settings needed for the algorithm to run on, so
% that only this file should ever be modified.

%------------------------PARAMETERS PREPARATION----------------------------
disp('Parameters preparation');
% spatial and temporal blocks length ad each iteration
X_BLOCK_LENGTH = [32, 4, 4, 2, 1, 2];
Y_BLOCK_LENGTH = [32, 4, 4, 2, 1, 2];
T_BLOCK_LENGTH = [48, 24,  8,  4, 2, 2];

% motion detection threshold, calculated as variance on the block over time
VARIANCE_THRESHOLD = [10, 20, 10, 10, 10];

% number of mask iterations
N_ITERATIONS = 5;

% edge calculations settings
EDGE_THRESHOLD = 0.4; %should be dependant on the expected motion blur
EDGE_METHOD = 'Canny';

% final filtering of the mask settings
FILTER_ON = true;
FILTER_SIGMA = [20, 20, 1];
FILTER_THRESHOLD = 0.1;

% final difference between original frames and estimated background
DIFFERENCE_CHANGE_THRESHOLD = 10;

% maxinum number of offsets, just used for pre-allocation of memory
MAX_OFFSETS_NUM = 100000;


%---------------------VIDEO PREPARATION-------------------------------
disp('Frames preparation');
vid = VideoReader(video);
height = vid.Height;
width = vid.Width;
ncolors = 3;
nframes = vid.NumberOfFrames;
nframes = nframes - mod(nframes,T_BLOCK_LENGTH(1)); % crop excess frames... we could just copy the last frame
height = height + Y_BLOCK_LENGTH(1) - mod(height, Y_BLOCK_LENGTH(1));
width = width +X_BLOCK_LENGTH(1) - mod(width, X_BLOCK_LENGTH(1));
frames = zeros([height, width, ncolors, nframes], 'uint8');
vid = VideoReader(video);
for i = 1:nframes
    frames(:,:,:,i) = imresize(uint8(vid.readFrame),[height, width]);
end


%--------------------------EDGE CREATION-----------------------------------
disp('Edge mask creation');
edge_mask = zeros([height, width, nframes],'logical');

my_edge = @(frm) edge(frm(:,:,1), EDGE_METHOD, EDGE_THRESHOLD)+ ...
    edge(frm(:,:,2),EDGE_METHOD,EDGE_THRESHOLD) + ...
    edge(frm(:,:,3), EDGE_METHOD, EDGE_THRESHOLD);

% could use arrayfun?
for (t = 1:nframes) 
    % SAREBBE TOP SCRIVERE UN EDGE DETECTOR CHE PREFERISCE UN PO' IL MOTION
    % BLUR? meh dipende... basta avere preferenza per i "soft edges"
    % E SAREBBE ANCHE TOP FARE IN PARALLELO GRRR ARRAYFUN CHE PROBLEMI HA
    edge_mask(:,:,t) = (my_edge(frames(:,:,:,t)) >= 1);
end


%------------------READ BACKGROUND-----------------------------
disp('Background reading');
new_background = imread(newBackground);