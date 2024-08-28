# "ported" by Adam Miller <maxamillion@fedoraproject.org> from
#   https://github.com/fedora-cloud/Fedora-Dockerfiles
#
# Originally written for Fedora-Dockerfiles by
#   scollier <scollier@redhat.com>

FROM centos:centos7
MAINTAINER The CentOS Project <cloud-ops@centos.org>

RUN yum -y update; yum clean all
RUN yum -y install openssh-server openssh-clients passwd sudo net-tools; yum clean all
ADD start.sh /start.sh
RUN chmod 755 /start.sh
RUN ./start.sh
RUN echo "root:shroot" | chpasswd
RUN echo "%netsim ALL=(root) NOPASSWD: ALL" > /etc/sudoers.d/netsim

ADD resources /home/netsim
RUN chmod 755 /home/netsim/*

RUN mkdir /var/run/sshd

RUN ssh-keygen -t rsa -f /etc/ssh/ssh_host_rsa_key -N ''

EXPOSE 22
ENTRYPOINT ["/usr/sbin/sshd", "-D"]
