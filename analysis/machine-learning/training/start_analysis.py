import sys
import numpy as np
from acceleration_dataset import AccelerationDataset
import augmentation
import mlp_model


def decode_measurement_data(encoded):
    # TODO: Implement decoding. CSV? protobuf?
    pass

def fetch_data_from_cassandra(user_id, conf):
    from cassandra.cluster import Cluster

    # Setup cassandra connection
    cluster = Cluster(conf.cassandra_cluster)
    session = cluster.connect(conf.keyspace)

    measurements = session.execute(
        """SELECT * FROM {column} WHERE user_id={user}""",
        {'column': conf.column_family, 'user': user_id}
    )

    train_data = []
    labels = []

    # TODO: Make sure this actually corresponds to the cassandra columns
    for (user_id, measurement_id, label, encoded_data) in measurements:
        train_data.append(decode_measurement_data(encoded_data))
        labels.append(label)

    return np.vstack(train_data), np.array(labels)


def train_model_for(user_id, data_fetcher, conf):
    # Fetch and augment data
    train_data, labels = data_fetcher(user_id, conf)
    augmented_data, augmented_labels = augmentation.augment_examples(train_data, labels, conf['target_length'])

    dataset = AccelerationDataset(augmented_data, augmented_labels, conf['number_of_labels'])
    bin_model = mlp_model.MLPMeasurementModel().train(dataset)
    return bin_model


def main():
    """Main entry point. Connects to cassandra and starts training."""

    # TODO: Where to get cluster ip from?
    # TODO: Which keyspace?
    conf = {
        'cassandra_cluster': ['192.168.0.1'],
        'keyspace': 'ak-lambda',
        'column_family': 'TODO',
        'target_length': 400,
        'number_of_labels': 29
    }

    # TODO: Define proper userid
    user_id = "42"

    # TODO: Figure out what to do with the model
    model = train_model_for(user_id, fetch_data_from_cassandra, conf, )

if __name__ == '__main__':
    sys.exit(main())
