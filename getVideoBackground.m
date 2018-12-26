function mean = getVideoBackground(video)
%TODO sosituire con mediana
%% VIDEO SETTINGS
%video = 'multipic2.mp4';
VObj=VideoReader(video);
% get number of frames
numFrames = get(VObj, 'NumberOfFrames');
% get frame rate
frameRate = get(VObj,'FrameRate');
VObj=VideoReader(video);
% get number of frames
numFrames = get(VObj, 'NumberOfFrames');
%skip some frames to be faster
skip = 1;
%get sizes
ff = read(VObj,1);
[ysize,xsize,~] = size(ff);
mean = zeros(ysize,xsize,3);
%the background should be the median of all the frames. Here it is calculated with
%medium value
%% GET BACKGROUND
for index = 1:skip:numFrames
    %disp(index);
    thisDoubleFrame = double(read(VObj,index));
    mean = mean + thisDoubleFrame./numFrames; %this separates the color layers
end
mean = uint8(mean);
end