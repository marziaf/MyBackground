%% PREPARAZIONE IMPOSTAZIONI

disp('->setting parameters');

% LA GROSSA HACK POTREBBE ESSERE AVERE BLOCCHETTI TANTO LUNGHI CON VARIANZA
% ALTA PER SELEZIONARE BENE CIò CHE SI MUOVE E BUTTARE VIA UNA GROSSA FETTA
% DI SFONDO, POI OVVIAMENTE FARLI PICCOLI MA PIù GENTILI?
% dimensioni dei blocchetti 3D, si potrebbe deciderlo in base ad un
% divisore delle dimensioni.. oppure croppo violento?
X_BLOCK_LENGTH = XBL_PARAMS(param_row,:);
Y_BLOCK_LENGTH = YBL_PARAMS(param_row,:);
T_BLOCK_LENGTH = TBL_PARAMS(param_row,:);

% threshold di rilevazione movimento come varianza sul blocchettino
VARIANCE_THRESHOLD = VTH_PARAMS(param_row,:);

N_ITERATIONS = N_ITERATIONS_PARAMS(param_row);

% threshold da applicare per il calcolo degli edge
EDGE_THRESHOLD = ETH_PARAMS(param_row); %calcolabile in base al motion blur che mi aspetto...
EDGE_METHOD = EMETH_PARAMS(param_row,:);

FILTER_SIGMA = SIGMA_PARAMS(param_row, :);
FILTER_THRESHOLD = FILTER_THRESHOLD_PARAMS(param_row);

% numero di offset massimo che potrebbero esserci nell'immagine, non tanto
% alto perchè alla fine resizzo i calcoli ogni volta...
MAX_OFFSETS_NUM = 100000;