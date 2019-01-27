%% PREPARAZIONE VIDEO 
%!!disp(['->video preparation of ',input_name]);
vid = VideoReader(input_name);
height = vid.Height;
width = vid.Width;
ncolors = 3;
nframes = vid.NumberOfFrames;
nframes = nframes - mod(nframes,T_BLOCK_LENGTH(1));
frames = zeros([height, width, ncolors, nframes], 'uint8');
vid = VideoReader(input_name);
%!!disp('->loading frames...');
for i = 1:nframes
    %si potrebbe semplicemente lavorare in uint8... meno casini...
    frames(:,:,:,i) = uint8(vid.readFrame);
end

%sistemo nel caso il numero di frame, la width o altro non vadano bene
%sistemiamo il numero di frames
% disp('->padding frames to have time dimensions match');
% while (mod(nframes,T_BLOCK_LENGTH(1)) > 0)
%     frames(:,:,:,nframes+1) = frames(:,:,:,nframes);
%     nframes = nframes + 1;
% end
%!!disp('->cropping image to have width and height match');
height = height - mod(height, Y_BLOCK_LENGTH(1));
width = width - mod(width, X_BLOCK_LENGTH(1));
frames = frames(1:height, 1:width, :, :);