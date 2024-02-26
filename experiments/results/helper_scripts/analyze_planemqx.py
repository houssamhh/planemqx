import os
import pandas as pd
import json

################# VARIABLES ###################

# type = 'normal'
type = 'priority'
input= '../default_config/input/default_config.json'
output = '..planemqx/output/'

###############################################
# _summary_
# This script is used to calculate the average response time per subscription for the planemqx with and without the priority.
# It takes as input the responsetimes.csv file and calculates the average response time per subscription.
# The results are saved in average_categorized_response_time.csv file in the same directory of the responsetimes.csv file. 


with open(input, 'r') as f:
    data = json.load(f)

load = {}
for device in data['IoTdevices']:
    for topic in device['publishesTo']:
        load[topic] = device['publishFrequency']*300
def categorize_app(app):
    if app.endswith(('13', '14', '15', '16')):
        return 'ST'
    elif app.endswith(('9', '10', '11', '12')):
        return 'TS'
    elif app.endswith(('1', '2', '3', '4')):
        return 'AN'
    elif app.endswith(('5', '6', '7', '8')):
        return 'RT'
    else:
        return None

def read_csv_files(directory):
    for subdir in os.listdir(directory):
        if subdir.startswith('bandwidth'):
            filepath = os.path.join(directory, subdir, 'responsetimes.csv')
            if os.path.exists(filepath):
                df = pd.read_csv(filepath, header=None, names=['timestamp', 'topic_app', 'responsetime'])
                df[['topic', 'app']] = df['topic_app'].str.split('/', expand=True)
                sent_count = df.groupby(['topic', 'app']).size().reset_index(name='received_count')                
                total_messages = pd.merge(sent_count, pd.DataFrame(load.items(), columns=['topic', 'message_sent']), on='topic')
                print(f"Total messages sent and load for {filepath}:")
                print(total_messages)
                
                # new_filepath = filepath.replace('/', '_').replace('\\', '_')
                new_filepath = filepath.split('.csv')[0].strip('/responsetimes') + '/total_messages.csv'

                total_messages.to_csv(new_filepath)
                df = pd.read_csv(filepath, header=None, names=['timestamp', 'subscription', 'response_time'])                
                df['timestamp'] = pd.to_datetime(df['timestamp'], unit='ms')
                df.set_index('timestamp', inplace=True)
                min_timestamp = df.index.min() + pd.Timedelta(milliseconds=60000)
                df=df[df.index >= min_timestamp]
                df[['topic', 'app']] = df['subscription'].str.split('/', expand=True)
                
                response_time_stats = df.groupby(['topic', 'app'])['response_time'].agg(['mean','min','max','std']).reset_index()

                print("Response time statistics")
                print(response_time_stats)
                # path = "response_time" + filepath
                # new_filepath = path.replace('/', '_').replace('\\', '_')
                # remove last part after / and add _response_time.csv
                new_filepath = filepath.split('.csv')[0].strip('/responsetimes') + '/average_response_time.csv'            
                response_time_stats.to_csv(new_filepath)
                path = os.path.join(directory, subdir)
                for filename in os.listdir(path):
                    if filename.startswith("average_re") and not os.path.isdir(filename) :
                        file_path = os.path.join(path, filename)
                        df = pd.read_csv(file_path)
                        df['category'] = df['app'].apply(categorize_app)
                        df = df[['topic', 'app', 'category', 'mean','min','max','std']]
                        print(df)
                        df.to_csv(file_path.replace('average', 'average_categorized'))
                os.remove(new_filepath)

read_csv_files(output)