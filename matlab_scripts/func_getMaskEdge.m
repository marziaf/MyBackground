% funzione che ottiene la maschera a partire da unn tensore uint8
% h-by-w-by-3-nf , la dim dei blocchetti, che qualcuno ha già preparato
% come divisore delle dimensioni h w nf e la threshold di varianza
% ritorna la h-by-w-by-nf matrice con con i blocchi che superano la
% varianza ed i blocchetti che toccano con l'edge
function [save_mask, limit_blocks, limit_blocks_cont] = func_getMaskEdge(frms, edg,  Y_BLOCK_DIM, X_BLOCK_DIM, T_BLOCK_DIM, VAR_THRESH)
    
    dimns = size(frms);
    
    %prealloccazione maschera finale
    save_mask = zeros([dimns(1), dimns(2), dimns(4)],'logical');
    
    %si potrebbe prealloccare limit_blocks ma non so quanto grande sia... e
    %non vorrei dopo dovermi gestire il discorso "quanto è lungo..." dovrei
    %ritornarlo come variabile
    limit_blocks = zeros([1000, 3],'uint16');
    limit_blocks_cont = 0; %occhio che partendo da 1 ne ho uno in più!
    
    %prealloccazione matrice hxwx3 varianza
    variance = zeros([Y_BLOCK_DIM, X_BLOCK_DIM, 3],'double');
    
    %scorro nel tempo a gruppetti di T_BLOCK_LENGTH frames
    for (t = 1:T_BLOCK_DIM:(dimns(4)))

        %scorro nelle x a gruppetti (blocchetti) di X_BLOCK_LENGTH
        for (x = 1:X_BLOCK_DIM:(dimns(2)))

            %scorro nelle y a gruppetti (blocchetti) di Y_BLOCK_LENGTH
            for (y = 1:Y_BLOCK_DIM:(dimns(1))) 

                variance = var(double(frms(y:(y+Y_BLOCK_DIM-1), x:(x+X_BLOCK_DIM-1) ,:, t:(t+T_BLOCK_DIM-1))), 1,4);
                % variance potrei anche metterlo dentro l'if tanto non devo
                % salvarlo...
                if (any(variance > VAR_THRESH))
                    %setto i particolari 1 come da salvare nella maschera
                    save_mask(y:(y+Y_BLOCK_DIM-1), x:(x+X_BLOCK_DIM-1), t:(t+T_BLOCK_DIM-1)) = 1;
                else
                    continue;
                end
                
                % se per questo blocchetto passa il serpentone dell'edge...
                % ED HA SUPERATO IL TEST DELLA VARIANZA
                % appuntalo che poi lo useremo... si potrà poi applicarlo
                % frame-by-frame? mah...
                %notare che non è altro che un'accodamento.. solo che
                %matlab non li sa gestire...
                if (any(edg(y:(y+Y_BLOCK_DIM-1), x:(x+X_BLOCK_DIM-1), t:(t+T_BLOCK_DIM-1)),'all'))
                    limit_blocks(limit_blocks_cont+1,:) = uint16([y,x,t]);
                    limit_blocks_cont = limit_blocks_cont+1;
                end
            end

        end
        
    end
end
