%{v0.1 This program evaluates the differences between the
%frames and the background, creates a mask and replaces
%with the new background. Comparisons are very inefficients
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
sensitivity = 15; % more sensitivity -> less noise
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
outputVideo = VideoWriter('video/bov5.avi');
outputVideo.FrameRate = FrameRate;
open(outputVideo);
%% GET ALL THE FRAMES %TODO deal with sizes not multiple of block size
for index=1:numFrames-1 %iterate over frames
    disp("computing frame "+num2str(index));
    %read frames
    vidFrame1 = read(VObj,index);
    vidFrame2 = read(VObj,index+1);
    disp("got frames")
    %% CREATE MASK AND REPLACE BACKGROUND
    %build differences matrix and weigth
    diff = abs(vidFrame1-vidFrame2)./sensitivity;
    %sum the color errors
    diff = diff(:,:,1)+diff(:,:,2)+diff(:,:,3);
    diff = diff.^2;
    %make this matrix more homogeneus to reduce noise and catch smaller
    %movements
    %for row=1:blockSize:ysize-blockSize
    %    for col=1:blockSize:xsize-blockSize
    %        diff(row:row+blockSize,col:col+blockSize) = mean(diff,'all');
    %    end
    %end
    
    %get mask from diff
    %mask -> zero if sub with new background, one otherwise
    %newMask -> current mask from diff
    %mask -> sum of old and new
    mask(:,:,1)=diff&diff; mask(:,:,2)=mask(:,:,1); mask(:,:,3)=mask(:,:,1);
    %opposite mask
    nmask = round(~mask);
    %make integer for multiplication
    mask = uint8(mask);
    nmask = uint8(nmask);
    %blocks selection
    newFrame = vidFrame1.*mask;
    newBackgroundIm = newBack.*nmask;
    %add to video the overlap
    writeVideo(outputVideo,newFrame+newBackgroundIm);

    
end
close(outputVideo);
%end

