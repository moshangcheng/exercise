import pandas as pd
import numpy as np

data = {'name': ['Jason', 'Molly', 'Tina', 'Jake', 'Amy'],
        'year': [2012, 2012, 2013, 2014, 2014],
        'reports': [4, 24, 31, 2, 3],
        'coverage': [25, 94, 57, 62, 70]}
df = pd.DataFrame(data, index=['Cochice', 'Pima', 'Santa Cruz', 'Maricopa', 'Yuma'])

df.filter()
