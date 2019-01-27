%% APPLICAZIONE MASCHERA E SALVATAGGIO
% prodotto cartesiano della maschera sporco...
if (DEBUG_VIDEO_EXPORT) 
disp(['->applying filter mask lev',num2str(i)]);
% DEVO upcastare a uint8 il nostro save mask per poter fare il prodotto
% cartesiano
save_mask_uint8 = zeros(size(frames),'uint8');
save_mask_uint8(:,:,1,:) = uint8(save_mask);
save_mask_uint8(:,:,2,:) = save_mask_uint8(:,:,1,:);
save_mask_uint8(:,:,3,:) = save_mask_uint8(:,:,1,:);
clean_frames = frames.*save_mask_uint8;

folder = strcat('progetto\output\costbg\',...
    int2str(X_BLOCK_LENGTH),'-',int2str(T_BLOCK_LENGTH),'-',int2str(VARIANCE_THRESHOLD),'-3D',int2str(FILTER_ON),'-',num2str(FILTER_SIGMA));
filename = strcat(folder,'-',output_name,'-l',num2str(i));
disp(['->file saving in',filename,'.avi']);

%func_videoExport(clean_frames, filename, vid.FrameRate); 
%per risparmiare memoria!
sframes = size(frames);
nfrs= sframes(4);

VW = VideoWriter(filename);
VW.FrameRate = vid.FrameRate;
open(VW);

for (i = 1:nfrs)
    writeVideo(VW,clean_frames(:,:,:,i));
end
    
close(VW);
clear('clean_frames'); clear('save_mask_uint8'); 

disp('->export success');
end