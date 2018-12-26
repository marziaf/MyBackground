% in questa versione si è abbandonata l'idea della varianza e della cattura
% del movimento. Si basa sull'estrazione dello sfondo da parte di
% getVideoBackground e sul calcolo della differenza frame-sfondo
%. L'esecuzione è molto più veloce.
% L'idea è quella di aggiungere un edge detection per migliorare il
% risultato

% NON FUNZIONA ANCORA BENE

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
%get background
background = getVideoBackground(video);
%get size of the frames
[ysize,xsize,~] = size(background);
%precision parameters
dSensitivity = 40;
%image for new background
newBack=imread(newBackground); %TODO deal with different sizes

%% PREPARE VIDEO WRITER
% If target directory does not exist, create it
foldername = 'video';
if ~exist(foldername,'dir')
    mkdir(foldername);
end
%outputVideo = VideoWriter(strcat(foldername,'/',videoName,"_",backgroundName,".avi"));
%%TODO perchè ritorna errore folder video non esiste?
reducingFactor = 1;
outputVideo = VideoWriter('video/bov9.avi');
outputVideo.FrameRate = FrameRate/reducingFactor;
open(outputVideo);

%% ELAB
for index=1:reducingFactor:numFrames %iterate over frames
    if(mod(index,5)==0) 
        disp("computing... "+num2str(index/numFrames*100)+"%");
    end
    %read frames
    %% CALCULATE DIFFERENCE
    vidFrame = read(VObj,index);
    diff = double(vidFrame)-double(background); %cast to double to avoid sign problems
    diff2d = diff(:,:,1)+diff(:,:,2)+diff(:,:,3);
    diff2d = uint8(floor(diff2d./dSensitivity));
    %% CREATE MASK AND REPLACE BACKGROUND
    %mask -> zero if sub with new background, one otherwise
    nmask = diff2d&diff2d;
    %opposite mask
    mask = ~nmask;
    %make integer for multiplication
    mask = uint8(mask);
    nmask = uint8(nmask);
    %apply mask
    newFrame = vidFrame.*mask;
    newBackgroundIm = newBack.*nmask;
    finishedFrame = newFrame+newBackgroundIm;
    %noNoise(:,:,1) = medfilt2(finishedFrame(:,:,1));
    %noNoise(:,:,2) = medfilt2(finishedFrame(:,:,2));
    %noNoise(:,:,3) = medfilt2(finishedFrame(:,:,3));
    %add to video the overlap
    writeVideo(outputVideo,finishedFrame);
    %TODO filter noise
end
close(outputVideo);
%end

