% this section creates the mask which represents the background portion of
% every frame

% ------------------INPUT PARAMETERS------------------------------
% BACKGROUND CONSTRUCTION NEEDED!
% video -> name of the video
% newBackground -> name of the background to replace
% backMode -> modality to detect background ('median' (suggested), 'mean' (use only if little memory is available))
% gaussianity (To be set to default)-> gaussian factor for blurring (suggested 5)
% dSensitivity (To be set to default)-> sensitivity to changes between frame and background (suggested 20-40)

% --------------------------NOTES-------------------------
% The aim of this algorithm is to be as fast as possible.
% For this reason it requires good ligth conditions (no hard
% shadows) and enough movement for background interpolation.
% These specifics are not very limiting and allow the code to
% be fast and work fine in most situations.

%---------------NEW BACKGROUND PREPARATION---------------------- 
% get the size of video frames from the just calculated background
[nrows,ncols,~] = size(background); 
% resize new background if necessary
newBack = imresize(newBack, [nrows ncols]);

% FILTERS
% filters are used to correct video imperfections
% gaussian filter -> smooth image
gaussback = imgaussfilt(background,gaussianity);
% black&white background to make it easier to evaluate 
% the complessive distance between background and the frames
bwBack = rgb2gray(gaussback);
% mask filter for holes removal
disk = strel('disk',4,4);

%-------------------VIDEO WRITER PREPARATION----------------------
outputVideo = VideoWriter(video_out);
outputVideo.FrameRate = FrameRate;
open(outputVideo);

% -------------------ELABORATION----------------------------------

for index=1:numFrames %iterate over frames
    % progress status
    if (mod(index,20) == 0)
       progress = ((index/numFrames)*100); % not considering time for background construction
       % for background computing
       disp(progress)
    end
    
    % CALCULATE DIFFERENCE
    % get frame
    vidFrame = read(VObj,index); % read frame
    gaussframe = imgaussfilt(vidFrame,gaussianity); % apply gaussian filter
    % calculate difference
    bwFrame = rgb2gray(gaussframe); % grayascale to sum the differences on different color layers
    diff = imabsdiff(bwBack,bwFrame); % cast to double to avoid sign problems
    
    % put to zero differences lower than dSensitivity
    diff2d = uint8(floor(double(diff)./dSensitivity));
    
    % CREATE MASK AND REPLACE BACKGROUND
    % mask -> zero if to be substituted with new background, one otherwise
    mask = diff2d&diff2d; %calculate mask as it is
    mask = medfilt2(mask); %filter noise
    mask = imdilate(mask, disk); %dilate borders
    mask = imfill(mask,'holes'); %fill the holes
    % opposite mask
    nmask = ~mask;
    % make integer for multiplication
    mask = uint8(mask);
    nmask = uint8(nmask);
    
    %apply mask
    newFrame = vidFrame.*mask;
    newBackgroundIm = newBack.*nmask;
    finishedFrame = newFrame+newBackgroundIm;
    
    %add to video the overlap
    writeVideo(outputVideo,finishedFrame);
end
close(outputVideo);