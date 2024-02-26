import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import sys

# You should first run the analyze_planemqx.py (priority and normal) script first to generate the average_categorized_response_time.csv file.
# This script is used to plot the bar charts for the average response time per category for the default configuration vs. PlanEMQX

# Input and output variables
priority_data = pd.read_csv(f'default_config/output/bandwidth_10/average_categorized_response_time.csv')
normal_data = pd.read_csv(f'planemqx/output/bandwidth_10/average_categorized_response_time.csv')
output = sys.argv[1]

priority_data['priority'] = 'PlanEMQX'
normal_data['priority'] = 'Default'

data = pd.concat([normal_data, priority_data])

sns.barplot(x='category', y='mean', hue='priority', data=data,errorbar=None)
plt.ylabel('Response Time (ms)')
plt.xlabel('Application Category')

plt.savefig(output, bbox_inches='tight')