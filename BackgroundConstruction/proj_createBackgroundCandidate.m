% CREAZIONE DEL PRIMO BACKGROUND_CANDIDATE
% viene creato dalla maschera di primo livello, che deve apposta essere
% buona

%!!disp('starting bg_mask creation');
save_mask_full = zeros([height, width, 3, nframes],'logical');
save_mask_full(:,:,1,:) = save_mask;
save_mask_full(:,:,2,:) = save_mask_full(:,:,1,:);
save_mask_full(:,:,3,:) = save_mask_full(:,:,1,:);
background_candidate = uint8(~save_mask_full(:,:,:,1)).*frames(:,:,:,1);
background_candidate_mask = ~save_mask_full(:,:,:,1);
clear('save_mask_full');