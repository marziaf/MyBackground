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

%SHOUL FILL SMALL HOLES? YEEEES!


% calculate the final mask looking for pixels which are not the background
% could use arrayfun
for (f = 1:nframes)
    % ANY color component change should mean change! test for change along
    % the third dimension
    % the black parts should be saved nontheless... let's hobe the subject
    % is not black or just create another mask that saves the
    % undefined/probabily-subject region
    % SHOULD CHANGE COLOR PLANE TO YCbCr???
    mask(:,:,f) = any(abs((frames(:,:,:,f) - background_candidate)) > DIFFERENCE_CHANGE_THRESHOLD,3);
end

