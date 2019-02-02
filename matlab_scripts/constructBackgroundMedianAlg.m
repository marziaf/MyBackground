% estimate the background from the video using the median of the
% frames

disp("CONSTRUCTBACKGROUND RUNNING") %DEBUG
%---------------------BACKGROUND CONSTRUCTION----------------------------
if strcmp(backMode,'mean')
    % ATTENTION! mean is not the best solution, but can be calculated
    % with O(1) occupation of memory, so it's preferrable in case of 
    % long videos or little RAM. As this is used only to save space,
    % it doesn't use matlab 'mean' function, but manually calculates it.
    
    VObj = VideoReader(video); % This was needed because of the coexistence
    % of NumberOfFrames and hasFrame
    background = double(readFrame(VObj))./numFrames;
    while hasFrame(VObj) 
        background = background + double(readFrame(VObj))./numFrames;
    end
    background = uint8(background);
    
elseif strcmp(backMode,'median')
    % get all the frames
    frms = read(VObj, [1 numFrames]);
    background = median(frms,4);
else 
    %ERROR
    disp("BackMode you set is not valid")
end
