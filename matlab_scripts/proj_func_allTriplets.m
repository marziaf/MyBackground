% arr matrice di triple, n_el_arr dice la lunghezza di ogni riga, n
% la quantità totale di righe
function [res,res_len] = proj_func_allTriplets(arr, n_el_arr, n)
    res = zeros([100000,3],'uint16');
    res_len = 0;
    for (row = 1:n) %arrayfun? dovrebbe essere lo stesso visto che tanto non conta l'ordine di esec
        to_extract = 1:n_el_arr(row);
        res( res_len+to_extract,: ) = arr(row, to_extract, :);
        res_len = res_len + n_el_arr(row);
    end
end