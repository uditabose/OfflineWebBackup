# base image 
FROM ubuntu:14.04

# organization email
MAINTAINER IBM WHAC <whac@us.ibm.com>

# user for setting up the container environment
USER root


# configure environment for anaconda and python
ENV DEBIAN_FRONTEND noninteractive
ENV CONDA_DIR /opt/conda
ENV SPARK_HOME /opt/spark
ENV PATH $CONDA_DIR/bin:$PATH
ENV SHELL /bin/shell
ARG OS_USER=ubuntu
ENV JP_USER $OS_USER
ENV JP_UID 1000
ENV HOME_DIR /home/$JP_USER/
ARG SPARK_MASTER=192.168.0.3:7077


# create jupyter user
RUN useradd -m -s /bin/bash -N -u $JP_UID $JP_USER && \
	mkdir -p $CONDA_DIR && \
	chown $JP_USER $CONDA_DIR


# remove preinstalled and possibly interfaring libraries
RUN apt-get remove -yq \
	python3-minimal \
	python3.4\
	python3.4-minimal \
	libpython3-stdlib \
	libpython3.4-stdlib \
	libpython3.4-minimal


# install necesary software and libraries
RUN apt-get update -qq && \
	apt-get install -yq --no-install-recommends \
		wget \
		build-essential \
		ca-certificates \
		curl \
		git \
		python-dev \
		bzip2 \
		unzip \
		libsm6 \
		libcurl4-openssl-dev \
		pandoc \
		sudo \
    	locales \
    	libxrender1 \
    	software-properties-common \
    	jq \
		&& apt-get clean

# install java
RUN add-apt-repository -y ppa:webupd8team/java && \
	apt-get update -qq && \
	echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
	echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections && \
	apt-get install -yq --no-install-recommends oracle-java7-installer && \
	update-alternatives --config java

ENV JAVA_HOME /usr/lib/jvm/java-7-oracle
 

# install spark
ENV SPARK_VERSION 1.6.0
RUN cd /tmp && \
	wget --quiet https://www.apache.org/dist/spark/spark-$SPARK_VERSION/spark-$SPARK_VERSION-bin-hadoop2.6.tgz && \
	tar -xzvf spark-$SPARK_VERSION-bin-hadoop2.6.tgz && \
	mv spark-$SPARK_VERSION-bin-hadoop2.6 $SPARK_HOME && \
	rm spark-$SPARK_VERSION-bin-hadoop2.6.tgz && \
	chown $JP_USER $SPARK_HOME

# Install Tini
ENV TINI_VERSION v0.9.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /usr/bin/tini
RUN chmod +x /usr/bin/tini

# switch user
USER $JP_USER

RUN mkdir -p /home/$JP_USER/work && \
    mkdir -p /home/$JP_USER/.jupyter && \
    mkdir -p /home/$JP_USER/.local


# install conda
RUN cd /tmp && \
	mkdir -p $CONDA_DIR && \
	wget --quiet https://repo.continuum.io/miniconda/Miniconda3-3.19.0-Linux-x86_64.sh && \
	echo "9ea57c0fdf481acf89d816184f969b04bc44dea27b258c4e86b1e3a25ff26aa0 *Miniconda3-3.19.0-Linux-x86_64.sh" | sha256sum -c - && \
        /bin/bash Miniconda3-3.19.0-Linux-x86_64.sh -f -b -p $CONDA_DIR && \
        rm Miniconda3-3.19.0-Linux-x86_64.sh && \
        $CONDA_DIR/bin/conda install --yes conda==3.19.1

# install jupyter notebook
RUN conda install --yes \
	'notebook=4.1*' \
	terminado \
	&& conda clean -yt

# install Python 3 packages
RUN conda install --yes \
    'ipywidgets=4.1*' \
    'pandas=0.17*' \
    'matplotlib=1.5*' \
    'scipy=0.17*' \
    'seaborn=0.7*' \
    'scikit-learn=0.17*' \
    && conda clean -yt


# install Python 2 packages and kernel spec
RUN conda create -p $CONDA_DIR/envs/python2 python=2.7 \
    'ipython=4.1*' \
    'ipywidgets=4.1*' \
    'pandas=0.17*' \
    'matplotlib=1.5*' \
    'scipy=0.17*' \
    'seaborn=0.7*' \
    'scikit-learn=0.17*' \
    pyzmq \
    && conda clean -yt

#install Jupyter console
RUN pip install jupyter-console

RUN jupyter notebook --generate-config
RUN echo "c.NotebookApp.ip = '*'" >> $HOME_DIR/.jupyter/jupyter_notebook_config.py

# spark communication
ENV PYTHONPATH $SPARK_HOME/python:$SPARK_HOME/python/lib/py4j-0.9-src.zip
ENV PYTHONSTARTUP $SPARK_HOME/python/pyspark/shell.py
ENV PYSPARK_SUBMIT_ARGS "--master spark://$SPARK_MASTER  pyspark-shell"

RUN bash -c '. activate python2 && \
    python -m ipykernel.kernelspec --prefix=$CONDA_DIR && \
    . deactivate'

    
# Set PYSPARK_HOME in the python2 spec
RUN jq --arg v "$CONDA_DIR/envs/python2/bin/python" \
        '.["env"]["PYSPARK_PYTHON"]=$v' \
        $CONDA_DIR/share/jupyter/kernels/python2/kernel.json > /tmp/kernel.json && \
        mv /tmp/kernel.json $CONDA_DIR/share/jupyter/kernels/python2/kernel.json


# Spark kernel
RUN mkdir -p $CONDA_DIR/share/jupyter/kernels/pyspark
COPY kernel.json /opt/conda/share/jupyter/kernels/pyspark
#RUN jupyter kernelspec install $HOME_DIR/work/pyspark
#RUN rm -rf $HOME_DIR/work/pyspark

# switch user to start notebook
USER root

# start the notebook

EXPOSE 8888
WORKDIR /home/$JP_USER/work
ENTRYPOINT ["tini", "--"]
CMD ["jupyter", "notebook", "--port=8888", "--no-browser", "--ip=0.0.0.0"]

RUN chown -R $JP_USER:users /home/$JP_USER/.jupyter/

USER $JP_USER

