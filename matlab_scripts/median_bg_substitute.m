% this will be the actual file called by java to execute the median-based
% algorithm

% use the final settings file to create the parameters to be used
prepareSettingsMedianAlg;

% start the actual algorithm that extracts the background
constructBackgroundMedianAlg;

% do the final "frame-by-frame" mask creation and write it to file
createMaskFromBackgroundMedianAlg;

clear all;