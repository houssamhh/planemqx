import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import sys

# This script is used to plot box plots for the average response time per category for PlanEMQX vs. the default configuration.
# You should first run the analyze_planemqx.py script first to generate the average_categorized_response_time.csv file.



fig, (ax1, ax2) = plt.subplots(1, 2, sharey=True)

data1 = pd.read_csv('default_config/output/bandwidth_10/average_categorized_response_time.csv')
data2 = pd.read_csv('planemqx/output/bandwidth_10/average_categorized_response_time.csv')
output = sys.argv[1]

sns.boxplot(x='category', y='mean', data=data1, showfliers=False, ax=ax1)
sns.boxplot(x='category', y='mean', data=data2, showfliers=False, ax=ax2)

ax1.set_ylabel('Response time (ms)')
ax1.set_xlabel('Normal')
ax2.set_xlabel('Priority')
ax2.set_ylabel('')

plt.savefig(output)
