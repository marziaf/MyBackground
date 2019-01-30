% this script creates the necessary settings and parameters that the
% median-based algorithm lives on

% inputs and outputs should be prepared beforehand.... let's make sure that's
% happening!
% instance_num = 1
% video = strcat('vid',instance_num,'.avi'); % name of the video
% newBackground = strcat('new_bg',int2str(instance_num),'.png'); % name of the background to replace
% video_out = strcat('new_vid',int2str(instance_num),'.png');
backMode = 'median'; % method to detect background ('median' (suggested), 'mean' (use only if little memory is available))
gaussianity = 5; % gaussian factor for blurring (suggested 5)
dSensitivity = 30; % sensitivity to changes between frame and background (suggested 20-40)
muchRAM = true;

vidObj = VideoReader(video);
numFrames = get(VObj, 'NumberOfFrames');
vidObj = VideoReader(video); %NumberOfFrames sometimes "consumes the frames", reload
FrameRate = get(VObj,'FrameRate');

% image for new background
newBack=imread(newBackground);