import numpy as np
from neon.datasets.dataset import Dataset
import logging

class AccelerationDataset(Dataset):
    logger = logging.getLogger("analysis.AccelerationDataset")

    # Number of features per example
    feature_count = None

    # Number of examples
    num_train_examples = None
    num_test_examples = None

    # Number of classes
    num_labels = None

    def __init__(self, examples, labels, num_lables):
        num_test = 500

        flattened_examples = self.flatten2d(examples)
        oh_labels = self.labels_as_one_hot(labels)

        self.num_labels = num_lables

        self.num_train_examples = flattened_examples.shape[0]
        self.num_test_examples = num_test
        self.feature_count = flattened_examples.shape[1]
        self.X_train = flattened_examples
        self.y_train = oh_labels
        self.X_test = flattened_examples[0:num_test, :]
        self.y_test = oh_labels[0:num_test, :]

    @staticmethod
    def flatten2d(npa):
        """Take a 3D array and flatten the last dimension."""
        return npa.swapaxes(0, 1).reshape((npa.shape[0],-1))

    @staticmethod
    def labels_as_one_hot(labels, num_classes):
        """Convert an integer representation to a one-hot-vector."""
        y = np.zeros((labels.shape[0], num_classes))
        for i in range(0, labels.shape[0]):
            y[i, labels[i]] = 1
        return y

    @staticmethod
    def as_int_rep(one_hot):
        """Converts an one-hot-vector to an integer representation."""
        return np.where(one_hot == 1)[0][0]

    def load(self, **kwargs):
        """Get the dataset ready for Neon training."""

        # Assign training and test datasets
        # INFO: This assumes the data is already shuffled! Make sure it is!
        self.inputs['train'] = self.X_train
        self.targets['train'] = self.y_train

        self.inputs['test'] = self.X_test
        self.targets['test'] = self.y_test

        self.format()
