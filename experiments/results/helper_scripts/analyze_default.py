import os
import pandas as pd
import json
import glob

################# VARIABLES ###################
input= '../default_config/input/scenario_load8.json'
output = '../default_config/output'
###############################################
# _summary_
# This script is used to analyze the response time of the the emqx without using the agent to prioritize the messages.
# It takes as input the responsetimes.csv file and calculates the average response time per subscription.
# The results are saved in averages.csv file in the same directory of the responsetimes.csv file.
# 

with open(input, 'r') as f:
    data = json.load(f)

load = {}
for device in data['IoTdevices']:
    for topic in device['publishesTo']:
        load[topic] = device['publishFrequency']*300
def read_csv_files(directory):
    for subdir in os.listdir(directory):
        if subdir.startswith('bandwidth'):
            filepath = os.path.join(directory, subdir, 'responsetimes.csv')
            if os.path.exists(filepath):
                df = pd.read_csv(filepath, header=None, names=['timestamp', 'topic', 'responsetime'])
                sent_count = df.groupby(['topic']).size().reset_index(name='received_count')                
                total_messages = pd.merge(sent_count, pd.DataFrame(load.items(), columns=['topic', 'message_sent']), on='topic')
                print(f"Total messages sent and load for {filepath}:")
                print(total_messages)
                
                new_filepath = filepath.split('.csv')[0].strip('/responsetimes') + '/total_messages.csv'

                total_messages.to_csv(new_filepath)
                df = pd.read_csv(filepath, header=None, names=['timestamp', 'subscription', 'response_time'])                
                df['timestamp'] = pd.to_datetime(df['timestamp'], unit='ms')
                df.set_index('timestamp', inplace=True)
                min_timestamp = df.index.min() + pd.Timedelta(milliseconds=60000)
                df=df[df.index >= min_timestamp]
                df[['topic']] = df['subscription'].str.split('/', expand=True)
                
                response_time_stats = df.groupby(['topic'])['response_time'].agg(['mean','min','max','std']).reset_index()

                print("Response time statistics")
                print(response_time_stats)
                new_filepath = filepath.split('.csv')[0].strip('/responsetimes') + '/average_response_time.csv'            
                response_time_stats.to_csv(new_filepath)

read_csv_files(output)
averages = {}

for subdir in os.listdir(output):
    if subdir.startswith('bandwidth'):
        filepath = os.path.join(output, subdir, 'average_response_time.csv')
        if os.path.exists(filepath):
            df = pd.read_csv(filepath)
            bandwidth = filepath.split(os.sep)[-2]
            averages[bandwidth] = df['mean'].mean()
            print("average for " + bandwidth + " is " + str(averages[bandwidth]))
averages_df = pd.DataFrame({'bandwidth': list(averages.keys()), 'average response time': list(averages.values())})
averages_df['bandwidth_num'] = averages_df['bandwidth'].str.extract('(\d+)').astype(int)
averages_df.sort_values('bandwidth_num', inplace=True)
averages_df.drop('bandwidth_num', axis=1, inplace=True)
# averages_df = averages_df.iloc[1:]
averages_df.to_csv(output + '/averages.csv', index=False)