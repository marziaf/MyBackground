% this section creates the mask which represents the background portion of
% every frame

%-----------------------BACKGROUND PREPARATION--------------------------
% resize new background if necessary
disp('last new background adjustments');
new_background = imresize(new_background, [height, width]);


%-----------------FINAL FRAME-BY-FRAME MASK CREATION---------------------
disp('final frambe-by-frame mask creation');
% preallocation
mask = zeros([height, width, nframes],'logical');

% transform our candidate
background_candidate_ycc = rgb2ycbcr(background_candidate);

% calculate the final mask looking for pixels which are not the background
% could use arrayfun
for (f = 1:nframes)
    % ANY YCbCr component change means change over the background
    actualFrameDiff = abs(rgb2ycbcr(frames(:,:,:,f)) - background_candidate_ycc); %3-plane image
    % mask(:,:,f) = any(actualFrameDiff > DIFFERENCE_CHANGE_THRESHOLD, 3);
    mask(:,:,f) = actualFrameDiff(:,:,1) > DIFFERENCE_CHANGE_THRESHOLD(1) | ...
                actualFrameDiff(:,:,2) > DIFFERENCE_CHANGE_THRESHOLD(2) | ...
                actualFrameDiff(:,:,3) > DIFFERENCE_CHANGE_THRESHOLD(3);
    % close small holes left by the algorithm 
    mask(:,:,f) = bwareaopen(mask(:,:,f), HOLES_MAX_AREA);
end

