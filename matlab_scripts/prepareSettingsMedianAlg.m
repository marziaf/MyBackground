% this script creates the necessary settings and parameters where the
% median-based algorithm lives on
%disp("PREPARESETTINGS RUNNING"); %DEBUG
%------------------------INPUT AND PARAMETERS PREPARATION----------------
% ~~~~~~~~~~~~~Algorithm parameters~~~~~~~~~~~~~~
% CAN BE MODIFIED ACCORDING TO NECESSITIES (better performances)
backMode = 'median'; % method to detect background ('median' (suggested), 'mean' (use only if little memory is available))
gaussianity = 5; % gaussian factor for blurring (suggested 5)
dSensitivity = 30; % sensitivity to changes between frame and background (suggested 20-40)
%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
% Video objects setting
pwd %DEBUG
disp(video); %DEBUG
VObj = VideoReader(video); % video name got from java code
numFrames = get(VObj, 'NumberOfFrames');
VObj = VideoReader(video); %NumberOfFrames sometimes "consumes the frames", reload
FrameRate = get(VObj,'FrameRate');

% image for new background
newBack=imread(newBackground);