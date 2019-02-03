% this will be the actual file called by java to execute the median-based
% algorithm
%disp("MEDIAN_BG_SUBSTITUTE RUNNING") %DEBUG
% use the final settings file to create the parameters to be used
prepareSettingsMedianAlg;

% progress keeps the value of the general progress of computation
progress = 0; % Doesn't consider time for background extraction
% start the actual algorithm that extracts the background
constructBackgroundMedianAlg;

% do the final "frame-by-frame" mask creation and write it to file
% here progress is updated
createMaskFromBackgroundMedianAlg;
progress = 100;

clear all;