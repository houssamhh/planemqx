import pandas as pd
import matplotlib.pyplot as plt
import sys

# This script is used to plot the average response time per bandwidth for the edict and the noagent.
# You should first run analyze_edict.py and analyze_default.py scripts first to generate the averages.csv files.



# Input and output files
edict_input ='edict/output/averages.csv'
emqx_input ='default_config/output/averages.csv'
output = sys.argv[1]


edict_df = pd.read_csv(edict_input)
edict_df['bandwidth_num'] = edict_df['bandwidth'].str.extract('(\d+)').astype(int)
edict_df.sort_values('bandwidth_num', inplace=True)
# edict_df.drop('bandwidth_num', axis=1, inplace=True)
edict_df.set_index('bandwidth_num', inplace=True)
edict_df.rename(columns={'average response time': 'Queueing simulations'}, inplace=True)

emqx_df = pd.read_csv(emqx_input)
emqx_df['bandwidth_num'] = emqx_df['bandwidth'].str.extract('(\d+)').astype(int)
emqx_df.sort_values('bandwidth_num', inplace=True)
emqx_df.set_index('bandwidth_num', inplace=True)
emqx_df.rename(columns={'average response time': 'Implementation'}, inplace=True)
merged_df = pd.merge(edict_df, emqx_df, left_index=True, right_index=True)
merged_df.plot(kind='line', rot=0, marker='o', legend=False)
#plt.ylim(0,100)
#plt.xlim(9,14)
plt.xlabel('Bandwidth (Mbps)')
plt.ylabel('Response Time (ms)')
plt.savefig(output, bbox_inches='tight')






