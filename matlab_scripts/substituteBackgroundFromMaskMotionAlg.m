% this section does the actual sostitution of the background


%----------------FINAL OUTPUT FRAMES CONSTRUCTION------------------
disp('final output frames construction');
%could just go full element-wise product, something like:
%new_frames = frames.*uint8(mask) + newBackground*uint8(not(mask));
% let's save some memory
new_frames = zeros(size(frames), 'uint8');
for (f = 1:nframes)
    new_frames(:,:,1,f) = frames(:,:,1,f).*uint8(mask(:,:,f)) + ...
        new_background(:,:,1).*uint8(~mask(:,:,f));
    new_frames(:,:,2,f) = frames(:,:,2,f).*uint8(mask(:,:,f)) + ...
        new_background(:,:,2).*uint8(~mask(:,:,f));
    new_frames(:,:,3,f) = frames(:,:,3,f).*uint8(mask(:,:,f)) + ...
        new_background(:,:,3).*uint8(~mask(:,:,f));
end



%-----------------------VIDEO WRITE---------------------------------
disp('Writing output video');
VW = VideoWriter(video_out);
VW.FrameRate = vid.FrameRate;
open(VW);

for (i = 1:nframes)
    writeVideo(VW,new_frames(:,:,:,i));
end
    
close(VW);