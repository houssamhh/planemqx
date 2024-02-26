import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import sys

# This script is used to plot a scatter plot for the average response time per topic for PlanEMQX vs. the default configuration.
# You should first run the analyze_planemqx.py (priority and normal) script first to generate the average_categorized_response_time.csv file.



# Input and output variables
priority_data = pd.read_csv(f'planemqx/output/bandwidth_10/average_categorized_response_time.csv') 
normal_data = pd.read_csv(f'default_config/output/bandwidth_10/average_categorized_response_time.csv')  
output = sys.argv[1]


priority_data['topic_number'] = priority_data['topic'].str.extract('(\d+)').astype(int)
normal_data['topic_number'] = normal_data['topic'].str.extract('(\d+)').astype(int)
data = pd.concat([priority_data, normal_data])
fig, (ax1, ax2) = plt.subplots(2, 1, sharex=True, sharey=True, figsize=(7, 5))
sns.scatterplot(x='topic_number', y='mean', data=normal_data, style='category', hue='category', ax=ax1)
ax1.get_legend().remove()
ax1.set_title('Normal')
ax1.set_ylabel('')
sns.scatterplot(x='topic_number', y='mean', data=priority_data, style='category', hue='category', ax=ax2)
# make the legend smaller
ax2.legend(loc='upper right', bbox_to_anchor=(1.14, 1.1))

ax2.set_title('Priority')
ax2.set_ylabel('')
x=0.04
fig.text(x, 0.5, 'Response time (ms)', va='center', rotation='vertical')
ax2.set_xlabel('Topic Id')
plt.xticks(data['topic_number'].unique())
plt.legend(prop={'size': 10})
plt.xticks(rotation=90)
plt.savefig(output)
plt.clf()

