%{v0.2 This program creates nxmx3xt=3 blocks, which store t=3 consecutive 
%frames. These are used to calculate the time variance, from which a mask
%is built
%}

video = 'multipic2.mp4';
newBackground = 'bovaro_resize.jpg';

%function select_figure(video, newBackground)

% get names without extension
[~,videoName,~] = fileparts(video);
[~,backgroundName,~] = fileparts(newBackground);
% create video object
VObj=VideoReader(video);
% get number of frames
numFrames = get(VObj, 'NumberOfFrames');
% get frame rate
FrameRate = get(VObj,'FrameRate');
bitPerPx = get(VObj,'BitsPerPixel');

%get first frame as background (to get sizes)
background = read(VObj,1); 
%get size of the frames
[ysize,xsize,~] = size(background);

%precision parameters
sensitivity = 100; % more sensitivity -> less noise
blockSize = 10;

%image for new background
newBack=imread(newBackground); %TODO deal with different sizes

% PREPARE VIDEO WRITER
% If target directory does not exist, create it
foldername = 'video';
if ~exist(foldername,'dir')
    disp('creating folder')
    mkdir(foldername);
end
%outputVideo =
%VideoWriter(strcat(foldername,'/',videoName,"_",backgroundName,".avi"));
%%TODO perchè ritorna errore folder video non esiste?
outputVideo = VideoWriter('video/bov7.avi');
outputVideo.FrameRate = FrameRate;
open(outputVideo);
%3D block -> 3 frames per time
timeBlock = zeros(ysize,xsize,3,3);
%% GET ALL THE FRAMES %TODO deal with sizes not multiple of block size
for index=1:numFrames-2 %iterate over frames (now it calculates a new mask for each frame, examining 3*numFrames frames)
    if(mod(index,5)==0) 
        disp("computing... "+num2str(index/numFrames*100)+"%");
    end
    %read frames
    vidFrame = read(VObj,index);
    timeBlock(:,:,:,1) = vidFrame;
    timeBlock(:,:,:,2) = read(VObj,index+1);
    timeBlock(:,:,:,3) = read(VObj,index+2);
    %calculate variance over time
    variance = var(timeBlock,1,4);
    %sum errors on different colors
    variance2d = variance(:,:,1)+variance(:,:,2)+variance(:,:,3);
    %weigth variance
    variance2d = floor(variance2d./sensitivity);
    
    %% CREATE MASK AND REPLACE BACKGROUND
    %get mask from diff
    %mask -> zero if sub with new background, one otherwise
    mask(:,:,1)=variance2d&variance2d; mask(:,:,2)=mask(:,:,1); mask(:,:,3)=mask(:,:,1);
    %opposite mask
    nmask = round(~mask);
    %make integer for multiplication
    mask = uint8(mask);
    nmask = uint8(nmask);
    %blocks selection
    newFrame = vidFrame.*mask;
    newBackgroundIm = newBack.*nmask;
    %add to video the overlap
    writeVideo(outputVideo,newFrame+newBackgroundIm);

    
end
close(outputVideo);
%end

