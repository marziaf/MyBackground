% this section does the actual sostitution of the background, it is common
% for both the algorithms


new_frames = frames.*mask + newBackground*not(mask);