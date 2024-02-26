import pandas as pd
import numpy as np

################# VARIABLES ###################
path = "../edict/output/"
###############################################

"""_Summary_
This script is used to analyze the response time of the edict results.
It takes as input the output.csv file and calculates the average response time per bandwidth.
The results are saved in averages.csv file in the same directory of the output.csv file.

"""

df = pd.read_csv(path + 'output.csv')
bandwidth_columns = [col for col in df.columns if col.startswith('bandwidth')]
# get the mean without including 0 values
averages = df[bandwidth_columns].replace(0, np.nan).mean()
# averages = df[bandwidth_columns].mean()
averages_df = pd.DataFrame({'bandwidth': averages.index, 'average response time': averages.values})
averages_df['bandwidth_num'] = averages_df['bandwidth'].str.extract('(\d+)').astype(int)
averages_df['average response time'] = averages_df['average response time'] * 1000
averages_df.sort_values('bandwidth_num', inplace=True)
averages_df.drop('bandwidth_num', axis=1, inplace=True)
# averages_df = averages_df.iloc[1:]
averages_df.to_csv(path + 'averages.csv', index=False)