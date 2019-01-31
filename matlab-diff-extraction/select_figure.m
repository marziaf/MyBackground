
%TODO Use varargin and logical indexing to detect non-specified arguments
%(e.g. empty) or check it on java code
function select_figure(video, newBackground, backMode, gaussianity, dSensitivity)
% video -> name of the video
% newBackground -> name of the background to replace
% backMode -> modality to detect background ('median' (suggested), 'mean' (use only if little memory is available))
% gaussianity -> gaussian factor for blurring (suggested 5)
% dSensitivity -> sensitivity to changes between frame and background (suggested 20-40)

%% ---------------STRUCTURAL PARAMETERS--------------------- 

%% DIRECTORIES AND FILES NAMES
% I/O dir
serverDir='../ServerBuffer/';
defaultInputDir = strcat(serverDir,'video_in');
defaultOutputDir = strcat(serverDir,'video_out');
defaultBackDir = strcat(serverDir,'backgrounds');

% get names without extension
[~,videoName,~] = fileparts(video);
[~,backgroundName,~] = fileparts(newBackground);

% file names/paths
inputName = strcat(defaultInputDir,'/',video);
outputName = strcat(defaultOutputDir,'/',videoName,'_',backgroundName,'.avi');
backName = strcat(defaultBackDir,'/',newBackground);

%VIDEO INPUT
% create video object
VObj=VideoReader(inputName);
% get number of frames
numFrames = get(VObj, 'NumberOfFrames');
% get frame rate
FrameRate = get(VObj,'FrameRate');

%% BACKGROUND
% get new background
disp('Getting background...');
background = getVideoBackground(inputName,backMode);
% image for new background
newBack=imread(backName);
[nrows,ncols,~] = size(background);
% resize new background if necessary
newBack = imresize(newBack, [nrows ncols]);

%% FILTERS
% gaussian filter
gaussback = imgaussfilt(background,gaussianity);
bwBack = rgb2gray(gaussback);
% mask filter for holes removal %TODO find best filter
disk = strel('disk',4,4);

%% PREPARE VIDEO WRITER
% If target directory does not exist, create it
if ~exist(defaultOutputDir,'dir')
    mkdir(defaultOutputDir);
end
outputVideo = VideoWriter(outputName);
outputVideo.FrameRate = FrameRate; %TODO perchè è così accelerato?
open(outputVideo);

%% -------------------ELABORATION----------------------------------

for index=1:numFrames %iterate over frames %TODO use readframe instead
    % progress "bar"
    if(mod(index,10)==0) 
        disp("computing... "+num2str(index/numFrames*100)+"%");
    end

    %% CALCULATE DIFFERENCE
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
    
    %% CREATE MASK AND REPLACE BACKGROUND
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

end

