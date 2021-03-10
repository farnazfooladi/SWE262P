import sys
import numpy as np

leetChar = "48<D3ƒ6#!9K1мИ0PQЯ57UVШ%¥2"
alphabetChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

characters = np.array([' ']+list(open(sys.argv[1]).read())+[' '])

characters[~np.char.isalpha(characters)] = ' '
characters = np.char.upper(characters)

sp = np.where(characters == ' ')

sp2 = np.repeat(sp, 2)

w_ranges = np.reshape(sp2[1:-1], (-1, 2))

w_ranges = w_ranges[np.where(w_ranges[:, 1] - w_ranges[:, 0] > 2)]

words = list(map(lambda r: characters[r[0]:r[1]], w_ranges))

swords = np.array(list(map(lambda w: ''.join(w).strip(), words)))

stop_words = np.array(list(set(open('stop_words.txt').read().upper().split(','))))
ns_words = swords[~np.isin(swords, stop_words)]

# one-to-one mapping
lc = np.char.translate(ns_words, str.maketrans(alphabetChar,leetChar))

# Calc 2-grams as stated above
leetWords = np.repeat(lc, 2)
result = np.reshape(leetWords[1:-1], (-1, 2))


uniq, counts = np.unique(result, axis=0, return_counts=True)
wf_sorted = sorted(zip(uniq, counts), key=lambda t: t[1], reverse=True)

for (w, c) in wf_sorted[:5]:
  print("{a} {b} - {c}".format(a = w[0], b = w[1], c = c))