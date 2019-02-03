%% PREPARAZIONE ED AFFINAMENTO MASCHERA EFFETTIVA
% preparo una matrice con i frame da salvare sporchi:
% è una matrice-maschera booleana che dice se un elemento (x,y,color,frame)
% deve appartenere al video in uscita, ovvero NON è SFONDO
%!!disp('->creating mask...');
save_mask = zeros(size(frames),'logical');

%creo il primo livello, poi accodo i blocchetti da salvare, che saranno
%salvati con le loro coordinate...
%!!disp(['->creating level 1 mask size (',num2str(Y_BLOCK_LENGTH(1)),',',...
%!!            num2str(X_BLOCK_LENGTH(1)),',',num2str(T_BLOCK_LENGTH(1)),') and level 2 offsets']);

% ottieni la maschera attual, quella a livello 1, e quali sono gli offset dei blocchetti che son
% toccati dal serpentone dell'edge (limblocks_offsets), e quanti sono (limblocks_number)
limblocks_offsets = zeros([MAX_OFFSETS_NUM, 3],'uint16');
limblocks_number = 0;
[save_mask, limblocks_offsets, limblocks_number] = ...
    func_getMaskEdge(frames, edge_mask, Y_BLOCK_LENGTH(1), X_BLOCK_LENGTH(1), T_BLOCK_LENGTH(1), VARIANCE_THRESHOLD(1));

i=1;

% ad ogni iterazione i di "pulizia", devi scorrere gli offset generati al
% livello i-1, ricalcolare la maschera con blocchetti più piccoli,
% generando altri offset da ripulire al livello i+1
if (N_ITERATIONS>1)
for (i = 2:N_ITERATIONS)
    
    %!!disp(['->creating level ', num2str(i), ' mask ']);
    
    %preallocco l'array che terrà, temporaneamente, tutti gli offset generati
    %al livello i, che saranno quindi da affinare al livello i+1
    % la prima dimensione dice quale offset precedente ha approfondito, la
    % seconda contiene le 3-ple da aggiungere al nuovo livello
    next_limblocks_offsets = zeros([MAX_OFFSETS_NUM, 1000, 3], 'uint16');
    next_limblocks_number = zeros([MAX_OFFSETS_NUM, 1], 'uint16');
     
    
    % scorriamo gli offset che il livello precedente ha generato, che sono
    % salvati in limblocks_offsets
    for (offset_iter = 1:limblocks_number)
        
        %stiamo, a questa offset_iter, lavorando con l'offset
        yxt = limblocks_offsets(offset_iter,:);
        y = yxt(1); x = yxt(2); t = yxt(3); 
        
        %!!disp(['->->level ',num2str(i), ', eval size (',num2str(Y_BLOCK_LENGTH(i-1)),',',...
        %!!    num2str(X_BLOCK_LENGTH(i-1)),',',num2str(T_BLOCK_LENGTH(i-1)),') @ offset (',...
        %!!    num2str(y),',',num2str(x),',',num2str(t),') -> ',num2str(offset_iter),'/',num2str(limblocks_number)]);
        
        
        % crea una maschera temporanea a partire da quel blocchetto, usando
        % come dimensione dei blocchetti il prossimo elemento nel vettore
        % delle dim dei blocchetti (che dovrà essere divisore, se no ciaone)
        % assegno anche la quantità di offset da aggiungere derivanti dal
        % precedente offset all'indice offset_iter
        [temp_save_mask, next_limblocks_offsets(offset_iter,:,:), next_limblocks_number(offset_iter)] = func_getMaskEdge(...
            frames(y:(y+Y_BLOCK_LENGTH(i-1)-1), x:(x+X_BLOCK_LENGTH(i-1)-1), :, t:(t+T_BLOCK_LENGTH(i-1)-1)) ,...
            edge_mask(y:(y+Y_BLOCK_LENGTH(i-1)-1), x:(x+X_BLOCK_LENGTH(i-1)-1), t:(t+T_BLOCK_LENGTH(i-1)-1)) ,...
            Y_BLOCK_LENGTH(i), X_BLOCK_LENGTH(i), T_BLOCK_LENGTH(i), VARIANCE_THRESHOLD(i));
        
        % next_limblocks_offsets ha ora, al livello offset_iter, gli
        % offsets generati da limblocks_offsets(offset_iter), che sono in
        % next_limblocks_number(offset_iter)
        % MA gli offset sono calcolati sui mini-blocchi dove gli ho
        % chiamato getMask! devo sommare (y,x,t) per ottenere gli offset reali
        next_limblocks_offsets(offset_iter, :, 1) = y-1 + next_limblocks_offsets(offset_iter, :, 1);
        next_limblocks_offsets(offset_iter, :, 2) = x-1 + next_limblocks_offsets(offset_iter, :, 2);
        next_limblocks_offsets(offset_iter, :, 3) = t-1 + next_limblocks_offsets(offset_iter, :, 3);
        
        %aggiorna la maschera con la nuova "precisione" sul blocchetto con
        %offset (y,x,t)... notare come abbiamo 1 solo piano colore, comune
        %per tutti è tre i piani RGB finali
        save_mask(y:(y+Y_BLOCK_LENGTH(i-1)-1), x:(x+X_BLOCK_LENGTH(i-1)-1), t:(t+T_BLOCK_LENGTH(i-1)-1)) = ...
            temp_save_mask;
        
    end
    
    % gli offsets da approfondire sono, alla prossima iterazione di
    % precisione, sono quelli che ho "accodato" nell'ultimo for, quelle
    % 3-ple salvate in next_limblocks_offsets
    %!!disp(['->preparing level ',num2str(i+1),' offsets']);
    [limblocks_offsets, limblocks_number] =...
         proj_func_allTriplets(next_limblocks_offsets, next_limblocks_number, limblocks_number);
    
    % bisogna anche ripulire la queue2, quella interna, dell'iterazione
    % appena fatta... la ripulisco già ad ogni inizio di iterazione sul
    % livello!
end
end
%!!disp('->highest level mask created');