"""Create new examples from existing one reproducing natural variation in the data"""

import numpy as np
import math

AUGMENTATION_START = 0.2
AUGMENTATION_END = 0.8

def augment_examples(examples, labels, target_length):
    """Augments all the passed examples (should be a np array with 3 dimensions)."""
    augmented = []
    augmented_labels = []
    for i in range(0, np.shape(examples)[0]):
        single_augmented, single_labels = augment_example(examples[i, :, :], labels[i], target_length)
        augmented.append(single_augmented)
        augmented_labels.append(single_labels)

    return np.vstack(augmented), np.vstack(augmented_labels)


def augment_example(example, label, target_length, window_step_size=5):
    """Example should be a numpy array, label a single label id."""
    sample_length = np.shape(example)[1]
    dimensions = np.shape(example)[2]

    # Those will define start and end of the data augmentation window, e.g. how far the window is moved over the data.
    # This assumes the first fraction of `AUGMENTATION_START` and the last fraction of `AUGMENTATION_END` measurement
    # points to be noise
    min_idx = sample_length * AUGMENTATION_START
    max_idx = sample_length * AUGMENTATION_END

    # Make sure we got enough data to get a complete example
    if (max_idx - min_idx) < target_length:
        start = (sample_length - target_length) / 2
        end = start + target_length
        return example[:, start:end, :], np.array(label)
    else:
        num_augmented = math.floor((max_idx-target_length-min_idx) / window_step_size) + 1
        print "Augmenting with", num_augmented, "examples"
        augmented = np.empty((num_augmented, target_length, dimensions))
        labels = np.empty(num_augmented)
        labels.fill(label)
        # Use a sliding window to move it over the input example. this will create new examples that can be used for
        # training a model
        for i in range(min_idx, max_idx-target_length, window_step_size):
            idx = (i-min_idx) / window_step_size + 1
            augmented[idx, :, :] = example[0, i:(i + target_length), :]
        return augmented, labels
