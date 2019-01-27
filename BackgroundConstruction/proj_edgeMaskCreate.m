%% PREPARAZIONE MASCHERA EDGE
% ha senso crearla per tutta la img o sarebbe lo stesso calcolarla nei
% singoli punti? scommetto lo stesso! ma effettivamente comunque il primo
% livello chiede di calcolarla quasi tutta...
%disp('->edge mask creation');
% prealloccazione
edge_mask = zeros([height, width, nframes],'logical');

%funzione carina e loopa per tutti... forse si può usare arrayfun
my_edge = @(frm) edge(frm(:,:,1), EDGE_METHOD, EDGE_THRESHOLD)+ ...
    edge(frm(:,:,2),EDGE_METHOD,EDGE_THRESHOLD) + ...
    edge(frm(:,:,3), EDGE_METHOD, EDGE_THRESHOLD);


for (t = 1:nframes) 
    %disp(['->->edge creation -> ',num2str(t),'/',num2str(nframes)]);
    %proviamo a forzare il motion blur come effetivo valore buono per la
    %edge detection...?
    % SAREBBE TOP SCRIVERE UN EDGE DETECTOR CHE PREFERISCE UN PO' IL MOTION
    % BLUR? meh dipende... basta avere preferenza per i "soft edges"
    % E SAREBBE ANCHE TOP FARE IN PARALLELO GRRR ARRAYFUN CHE PROBLEMI HA
    edge_mask(:,:,t) = (my_edge(frames(:,:,:,t)) >= 1);
end
