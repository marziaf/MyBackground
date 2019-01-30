% this section creates the mask which represents the background portion of
% every frame

% resize new background if necessary
[nrows,ncols,~] = size(background);
newBack = imresize(newBack, [nrows ncols]);

% filter the background obtained so that it more easily thresholds the
% differences 
gaussback = imgaussfilt(background,gaussianity);
bwBack = rgb2gray(gaussback);
% mask filter for holes removal %TODO find best filter
disk = strel('disk',4,4);