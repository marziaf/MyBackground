%{v0.2 This program creates nxmx3xt=2 blocks, which store t=2 consecutive 
%frames. These are used to calculate the time variance, from which a mask
%is built
%}

video = 'multipic2.mp4';
newBackground = 'black.jpg';

%function select_figure(video, newBackground)
%% STRUCTURAL PARAMETERS 
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
%get first frame as background
background = read(VObj,1); 
%get size of the frames
[ysize,xsize,~] = size(background);
%precision parameters
dSensitivity = 1; % more sensitivity -> less noise (zero not supported yet)
%image for new background
newBack=imread(newBackground); %TODO deal with different sizes

%% PREPARE VIDEO WRITER
% If target directory does not exist, create it
foldername = 'video';
if ~exist(foldername,'dir')
    disp('creating folder')
    mkdir(foldername);
end
%outputVideo = VideoWriter(strcat(foldername,'/',videoName,"_",backgroundName,".avi"));
%%TODO perchè ritorna errore folder video non esiste?
outputVideo = VideoWriter('video/bov9.avi');
outputVideo.FrameRate = FrameRate;
open(outputVideo);

%% ELAB
for index=1:numFrames-2 %iterate over frames (now it calculates a new mask for each frame, examining 3*numFrames frames)
    if(mod(index,5)==0) 
        disp("computing... "+num2str(index/numFrames*100)+"%");
    end
    %read frames
    %% CALCULATE DIFFERENCE
    vidFrame = read(VObj,index);
    timeBlock(:,:,:,1) = vidFrame;
    timeBlock(:,:,:,2) = read(VObj,t);
    %calculate difference over time
    diff = timeBlock(:,:,:,1)-timeBlock(:,:,:,2);
    %sum errors on different colors
    diff2d = diff(:,:,1)+diff(:,:,2)+diff(:,:,3);
    diff2d = uint8(floor(diff2d./2));
    %% CREATE MASK AND REPLACE BACKGROUND
    %mask -> zero if sub with new background, one otherwise
    nmask =(diff2d) & (diff2d);
    %opposite mask
    mask = ~nmask;
    %make integer for multiplication
    mask = uint8(mask);
    nmask = uint8(nmask);
    %pixels selectionarea defined by locations, where conn specifies the connectivity.

    newFrame = vidFrame.*mask;
    newBackgroundIm = newBack.*nmask;
    finishedFrame = newFrame+newBackgroundIm;
    %add to video the overlap
    writeVideo(outputVideo,finishedFrame);
    
end
close(outputVideo);
%end

