from neon.backends import gen_backend
from neon.layers import FCLayer, DataLayer, CostLayer, DropOutLayer
from neon.models import MLP
from neon.transforms import RectLin, Tanh, Logistic, CrossEntropy
from neon.experiments import FitPredictErrorExperiment
from neon.params import val_init
from os.path import expanduser, exists
from os import remove
import time
import logging
import utils


class MLPMeasurementModel(object):
    # General settings
    max_epochs = 100

    epoch_step_size = 5

    batch_size = 30  # max(10, min(100, dataset.num_train_examples/10))

    random_seed = 42  # Take your lucky number

    feature_count = 400

    num_labels = 29

    # Storage director of the model and its snapshots
    file_path = expanduser('~/data/workout-dl/workout-mlp.prm')

    if exists(file_path):
        remove(file_path)

    # Captured errors for the different epochs
    train_err = []
    test_err = []

    @staticmethod
    def learning_conf(current_epoch):
        if current_epoch < 100:
            lrate = 0.01
        elif current_epoch < 200:
            lrate = 0.003
        else:
            lrate = 0.001

        return {
            'lr_params': {
                'learning_rate': lrate,
                'momentum_params': {'coef': 0.9, 'type': 'constant'}},
            'type': 'gradient_descent_momentum'
        }

    print 'Epochs: %d Batch-Size: %d' % (max_epochs, batch_size)

    # Generate layers and a MLP model using the given settings
    def model_gen(self, last_epoch, max_epoch):

        lconf = self.learning_conf(last_epoch)
        layers = []

        layers.append(DataLayer(nout=self.feature_count))

        layers.append(FCLayer(
            name="fc_1",
            nout=500,
            lrule_init=lconf,
            weight_init=val_init.UniformValGen(low=-0.1, high=0.1),
            activation=RectLin()
        ))

        layers.append(DropOutLayer(
            name="do_1",
            keep=0.9
        ))

        layers.append(FCLayer(
            name="fc_2",
            nout=250,
            lrule_init=lconf,
            weight_init=val_init.UniformValGen(low=-0.1, high=0.1),
            activation=RectLin()
        ))

        layers.append(DropOutLayer(
            name="do_2",
            keep=0.9
        ))

        layers.append(FCLayer(
            name="fc_3",
            nout=100,
            lrule_init=lconf,
            weight_init=val_init.UniformValGen(low=-0.1, high=0.1),
            activation=RectLin()
        ))

        layers.append(DropOutLayer(
            name="do_3",
            keep=0.9
        ))

        layers.append(FCLayer(
            name="fc_4",
            nout=self.num_labels,
            lrule_init=lconf,
            weight_init=val_init.UniformValGen(low=-0.1, high=0.1),
            activation=Logistic()
        ))

        layers.append(CostLayer(
            name='cost',
            ref_layer=layers[0],
            cost=CrossEntropy()
        )
        )
        model = MLP(num_epochs=max_epoch, batch_size=self.batch_size, layers=layers, serialized_path=self.file_path)
        return model

    def train(self, dataset):
        # Set logging output...
        for name in ["neon.util.persist", "neon.datasets.dataset", "neon.models.mlp"]:
            dslogger = logging.getLogger(name)
            dslogger.setLevel(20)

        print "Starting training..."
        start = time.time()
        train_err = []
        test_err = []
        for current_epoch in range(0, self.max_epochs + 1 - self.epoch_step_size, self.epoch_step_size):
            next_max_epoch = current_epoch + self.epoch_step_size

            # set up the model and experiment
            model = self.model_gen(current_epoch, next_max_epoch)

            # Uncomment line below to run on CPU backend
            backend = gen_backend(rng_seed=self.random_seed)
            # Uncomment line below to run on GPU using cudanet backend
            # backend = gen_backend(rng_seed=0, gpu='cudanet')
            experiment = FitPredictErrorExperiment(model=model,
                                                   backend=backend,
                                                   dataset=dataset)

            # Run the training, and dump weights
            res = experiment.run()
            train_err.append(res['train']['MisclassPercentage_TOP_1'])
            test_err.append(res['test']['MisclassPercentage_TOP_1'])

            print "Finished epoch " + str(next_max_epoch)

        print "Finished training!"
        end = time.time()
        print "Duration", end - start, "seconds"
        return utils.get_bytes_from_file(self.file_path)
