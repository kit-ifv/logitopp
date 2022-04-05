# -*- coding: utf-8 -*-
"""
Created on Tue Mar 29 10:45:01 2022

@author: ar0305
"""

import pandas as pd
import random

animal = [
    'Anaconda',
    'Alligator',
    'Beaver',
    'Bear',
    'Cat',
    'Chicken',
    'Deer',
    'Dolphin',
    'Elephant',
    'Emu',
    'Ferret',
    'Fox',
    'Gecko',
    'Goose',
    'Horse',
    'Husky',
    'Iguana',
    'Ibis',
    'Jellyfish',
    'Jaguar',
    'Koala',
    'Kiwi',
    'Lemur',
    'Lion',
    'Mantis',
    'Mole',
    'Newt',
    'Numbat',
    'Opossum',
    'Octopus',
    'Puma',
    'Pig',
    'Quokka',
    'Quail',
    'Rhino',
    'Rat',
    'Shark',
    'Seal',
    'Tiger',
    'Turkey',
    'Uakari',
    'Vole',
    'Vulture',
    'Weasel',
    'Wolf',
    'Yak',
    'Yeti',
    'xerus',
    'Zebra'
]

attribute = [
    'awful',
    'Ambitious',
    'Balanced',
    'bad',
    'Consistent',
    'Clever',
    'Deceitful',
    'Dry',
    'Exciting',
    'Excellent',
    'Foolish',
    'Faithful',
    'Genuine',
    'Grateful',
    'Helpful',
    'Huge',
    'Intolerant',
    'Illegal',
    'Jealous',
    'Jolly',
    'kind',
    'keen',
    'Lazy',
    'Loud',
    'Modest',
    'Mature',
    'Nervous',
    'Narrow',
    'Organized',
    'Obese',
    'Pessimistic',
    'Poor',
    'Quick',
    'Quiet',
    'Resentful',
    'Remarkable',
    'Skilled',
    'Shiny',
    'Thoughtless',
    'Tasty',
    'Understanding',
    'Ugly',
    'Vain',
    'Vulgar',
    'Warm',
    'Wealthy',
    'xenogenic',
    'Young',
    'yellow',
    'zippy',
    'Zealous'
]

zones = [6113,6114,6115,6116,6121,6122,6133,6134,6135,6136,6141,6142,6152,6153,
         6154,6160,6170,6181,6182,6190,6210,6220,6230,6240,6250,6261,6262,6271,
         6272,6280,6320,6330,6361,6362,6415,6511,6512,6513,6514,6515,6523,6524,
         6533,6650,6660,6711,6712,6715]



df = pd.DataFrame(columns=['id','name','area','employees','branch',
                           'loc_x','loc_y','zone',
                           'function:1', 'function:2', 'function:3', 
                           'fleet:1', 'fleet:2', 'fleet:3'])


for i in range(1,101):
    name = random.choice(attribute).lower() + '_' + random.choice(animal).lower()
    area = random.gauss(30, 10)
    employees = random.randint(5, 70)
    branch = random.randint(1, 3)
    y = 49.01297208914998 + random.random() * 0.005
    x = 8.401463799315659 + random.random() * 0.1
    zone = random.choice(zones)
    fl1 = random.randint(5,30)
    fl2 = random.randint(5,30)
    fl3 = random.randint(5,30)
    
    f1 = random.random()
    f2 = (1-f1) * random.random()
    f3 = 1 - f1 - f2        
    
    df.loc[len(df)+1] = (i, name, area, employees, branch, x, y, zone, f1, f2, f3, fl1, fl2, fl3)
    
print(len(df.name.unique()))
df.to_csv('businesses.csv', sep=";")