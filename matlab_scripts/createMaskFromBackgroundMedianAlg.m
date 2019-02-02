% this section creates the mask which represents the background portion of
% every frame
% ------------------INPUT PARAMETERS------------------------------
% BACKGROUND CONSTRUCTION NEEDED!
% video -> name of the video
% newBackground -> name of the background to replace
% backMode -> modality to detect background ('median' (suggested), 'mean' (use only if little memory is available))
% gaussianity (To be set to default)-> gaussian factor for blurring (suggested 5)
% dSensitivity (To be set to default)-> sensitivity to changes between frame and background (suggested 20-40)

%---------------NEW BACKGROUND PREPARATION---------------------- 
% get the size of video frames from the just calculated background
[nrows,ncols,~] = size(background); 
% resize new background if necessary
newBack = imresize(newBack, [nrows ncols]);

% FILTERS
% filters are used to correct video imperfections
% gaussian filter -> smooth image
gaussback = imgaussfilt(background,gaussianity);
bwBack = rgb2gray(gaussback);
% mask filter for holes removal %TODO find best filter
disk = strel('disk',4,4);

%-------------------VIDEO WRITER PREPARATION----------------------
outputVideo = VideoWriter(video_out);
outputVideo.FrameRate = FrameRate; %TODO perch� � cos� accelerato?
open(outputVideo);

% -------------------ELABORATION----------------------------------

for index=1:numFrames %iterate over frames %TODO use readframe instead
    % CALCULATE DIFFERENCE
    % get frame
    vidFrame = read(VObj,index); % read frame
    gaussframe = imgaussfilt(vidFrame,gaussianity); % apply gaussian filter
    % calculate difference
    %TODO necessary grayscale? Efficent?
    %NOTE rgb2gray converts RGB values to grayscale values by forming a weighted sum of the R, G, and B components:
    %0.2989 * R + 0.5870 * G + 0.1140 * B 
    bwFrame = rgb2gray(gaussframe); % grayascale to sum the differences on different color layers
    %bwFrame = imadjust(bwFrame); % correct contrast to delete shadows
    %bwBack = imadjust(bwBack);
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