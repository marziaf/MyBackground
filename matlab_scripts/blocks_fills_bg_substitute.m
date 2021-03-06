% this will be the actual file called by java to execute the motion-based
% algorithm

%use the final settings file to create the parameters to be used
prepareSettingsMotionAlg;

%start the actual algorithm that extracts the background
constructBackgroundMotionAlg;

% do the final "frame-by-frame" mask creation
createMaskFromBackgroundMotionAlg;

% substitute the background from the video using the mask
substituteBackgroundFromMaskMotionAlg;

clear all;