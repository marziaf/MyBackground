% this will be the actual file called by java to execute the median-based
% algorithm

% use the final settings file to create the parameters to be used
prepareSettingsMedianAlg;

% start the actual algorithm that extracts the background
elaborateMedianAlg;

% do the final "frame-by-frame" mask creation
createMaskFromBackground;

% substitute the background from the video using the mask
substituteBackgroundFromMask;