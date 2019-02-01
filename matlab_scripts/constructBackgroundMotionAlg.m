%this section does the actual background estimate
N_ITERATIONS_BCP = N_ITERATIONS;


% ---------------------DRAFT BACKGROUND ESTIMATION-----------------------
disp('Draft background estimation');
% we could compress all the info using a mean/median to calculate the
% starting bg?
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
N_ITERATIONS = N_ITERATIONS_BCP;
proj_moveMaskCreate;


% -----------------------HIGH-LEVEL MASK FILTERING-----------------------
% smoothens out the mask for less noisy contours, making the ones too much
disp('high-level mask filtering');
filtered = imgaussfilt3(double(save_mask), FILTER_SIGMA);
maxfilt = max(filtered, [], [1,2]);
for (f = 1:nframes)
    filtered(:,:,f) = filtered(:,:,f)/maxfilt(f);
end
lala =  filtered>FILTER_THRESHOLD;

clip_fr_end = floor(0.95*nframes);
clip_fr_beg = floor(0.05*nframes);
save_mask(:,:,clip_fr_beg:clip_fr_end) = lala(:,:,clip_fr_beg:clip_fr_end);
%la salvo tutta per evitare che sporchi, cosi bg_mask diventa 0 totale alla
%fine e all'inizio e non sporca
save_mask(:,:,1:(clip_fr_beg-1)) = ones([height, width, clip_fr_beg-1],'logical');
save_mask(:,:,(clip_fr_end+1):nframes) = ones([height, width, nframes-clip_fr_end+1-1],'logical');
%notare che ? rinormalizzata da sola
FILTER_ON = 1;
i = 5;
clear('lala'); clear('filtered');


% -----------------BACKGROUND CANDIDATE CREATION----------------------
disp('Background candidate contruction');
bg_mask = ~save_mask;

% prealloccazione
to_add_mask = zeros(height, width, 3);
to_add_mask = zeros(height, width);
filled_mask = zeros(height, width, 3);
% scorriamo i frame e la maschera nel tempo per affinare bg_candidate
for (fr = 2:nframes)
    
    %!!disp(['->->construction of bg_mask -> ',num2str(fr)]);
    
    % per ogni frame calcolo la differenza (dove salvo solo il "positivo") 
    % tra la maschera dell'attuale frame e la machera su cui "vive" il
    % bg_candidate, che rappresenta la porzione di background "sicura" (che
    % sarebbe anche calcolabile all'istante ma non ha senso fare fatica
    mask_difference = bg_mask(:,:,fr) & ~background_candidate_mask;
    
    %per aggiornare la parte "nuova" E CONNESSA
      
    % verifichiamo la parte connessa al resto con un bucket fill
    % lo starting point deve essere parte del background_candidate
    starting_point = [floor(height/2),width];
    filled_mask(:,:,1) = (imfill(save_mask(:,:,fr) , starting_point)) & bg_mask(:,:,fr);
    filled_mask(:,:,2) = filled_mask(:,:,1);
    filled_mask(:,:,3) = filled_mask(:,:,1);
    % selezioniamo solo la parte nuova... logica come sopra
    to_add_mask = filled_mask & mask_difference;
    
    background_candidate_mask = background_candidate_mask | to_add_mask;
    background_candidate = background_candidate + (frames(:,:,:,fr).*uint8(to_add_mask));
    
    % problema: e se una maschera save non ? giusta e mangia un poco il
    % soggetto? devo avere dei bei parametri!
    imshow(background_candidate);
end