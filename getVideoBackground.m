function [mm, frms] = getVideoBackground(video,m) %video -> video name, m -> mean or median
%% VIDEO SETTINGS
%video = 'multipic2.mp4';
VObj=VideoReader(video);
% get number of frames
numFrames = get(VObj, 'NumberOfFrames');
% get frame rate
VObj=VideoReader(video);
% get number of frames
numFrames = get(VObj, 'NumberOfFrames');

% get all the frames
frms = read(VObj, [1 numFrames]);
if strcmp(m,'mean')
    mm = mean(frms,4);
elseif strcmp(m,'median')
    mm = median(frms,4);
else %TODO handle error
end

end