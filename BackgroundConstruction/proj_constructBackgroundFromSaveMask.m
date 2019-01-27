% per comodità creo la antimaschera del soggetto, che è la maschera del mio
% background
bg_mask = ~save_mask;

% prealloccazione
to_add_mask = zeros(height, width, 3);
to_add_mask = zeros(height, width);
filled_mask = zeros(height, width, 3);
% scorriamo i frame e la maschera nel tempo per affinare bg_candidate
for (fr = 2:nframes)
    
    %!!disp(['->->construction of bg_mask -> ',num2str(fr)]);
    
    % per ogni frame calcolo la differenza (dove salvo solo il "positivo") 
    % tra la maschera dell'attuale frame e la machera su cui "vive" il
    % bg_candidate, che rappresenta la porzione di background "sicura" (che
    % sarebbe anche calcolabile all'istante ma non ha senso fare fatica
    mask_difference = bg_mask(:,:,fr) & ~background_candidate_mask;
    
    %per aggiornare la parte "nuova" E CONNESSA
      
    % verifichiamo la parte connessa al resto con un bucket fill
    % lo starting point deve essere parte del background_candidate
    starting_point = [floor(height/2),width];
    filled_mask(:,:,1) = (imfill(save_mask(:,:,fr) , starting_point)) & bg_mask(:,:,fr);
    filled_mask(:,:,2) = filled_mask(:,:,1);
    filled_mask(:,:,3) = filled_mask(:,:,1);
    % selezioniamo solo la parte nuova... logica come sopra
    to_add_mask = filled_mask & mask_difference;
    
    background_candidate_mask = background_candidate_mask | to_add_mask;
    background_candidate = background_candidate + (frames(:,:,:,fr).*uint8(to_add_mask));
    
    % problema: e se una maschera save non è giusta e mangia un poco il
    % soggetto? devo avere dei bei parametri!
end

% puliamo da ciò che non serve, può essere comodo per minimizzare l'utilizzo
% di memoria durante l'esecuzione di altre robe, ma tanto poi vanno
% rialloccatte lo stesso... magari nella versione finale...
%clear('to_add_mask'); clear('bg_mask'); clear('mask_difference'); clear('to_add_mask');