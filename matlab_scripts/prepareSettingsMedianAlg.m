% this script creates the necessary settings and parameters that the
% median-based algorithm lives on

%------------------------INPUT AND PARAMETERS PREPARATION----------------
% inputs and outputs should be prepared beforehand.... let's make sure that's
% happening!
backMode = 'median'; % method to detect background ('median' (suggested), 'mean' (use only if little memory is available))
gaussianity = 5; % gaussian factor for blurring (suggested 5)
dSensitivity = 30; % sensitivity to changes between frame and background (suggested 20-40)

VObj = VideoReader(video);
numFrames = get(VObj, 'NumberOfFrames');
VObj = VideoReader(video); %NumberOfFrames sometimes "consumes the frames", reload
FrameRate = get(VObj,'FrameRate');

% image for new background
newBack=imread(newBackground);