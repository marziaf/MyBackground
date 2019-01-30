function M = getVideoBackground(video,m) %video -> video name, m -> mean or median
%% VIDEO SETTINGS
%video = 'multipic2.mp4';
VObj=VideoReader(video);
% get number of frames
numFrames = get(VObj, 'NumberOfFrames');

if strcmp(m,'mean')
    % ATTENTION! mean is not the best solution, but can be calculated
    % with O(1) occupation of memory, so it's preferrable in case of 
    % long videos or little RAM. As this is used only to save space,
    % it doesn't use matlab 'mean' function, but manually calculates it.
    
    VObj = VideoReader(video); % This was needed because of the coexistence
    % of NumberOfFrames and hasFrame
    M = double(readFrame(VObj))./numFrames;
    while hasFrame(VObj) 
        M = M + double(readFrame(VObj))./numFrames;
    end
    M = uint8(M);
    
elseif strcmp(m,'median')
    % get all the frames
    frms = read(VObj, [1 numFrames]);
    M = median(frms,4);
else %TODO handle error
end

end