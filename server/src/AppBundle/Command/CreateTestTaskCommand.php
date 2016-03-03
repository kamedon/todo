<?php

namespace AppBundle\Command;

use Proxies\__CG__\AppBundle\Entity\Task;
use Symfony\Bundle\FrameworkBundle\Command\ContainerAwareCommand;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Input\InputOption;
use Symfony\Component\Console\Output\OutputInterface;

class CreateTestTaskCommand extends ContainerAwareCommand
{
    protected function configure()
    {
        $this
            ->setName('todo:task:create')
            ->setDescription('Create Test Task')
            ->addOption('count', null, InputOption::VALUE_REQUIRED, 'create task count', 1)
            ->addOption('user_id', null, InputOption::VALUE_REQUIRED, 'user', 1);
    }

    protected function execute(InputInterface $input, OutputInterface $output)
    {
        $manger = $this->getContainer()->get("doctrine")->getManager();
        $repository = $this->getContainer()->get('doctrine')->getRepository("AppBundle:User");
        $user = $repository->find($input->getOption('user_id'));
        if (!$user) {
            $output->writeln('<error>not found user</error>');
            return;
        }
        for ($i = 0; $i < $input->getOption('count'); $i++) {
            $task = new Task();
            $task->setUser($user);
            $task->setBody($user."_test_task".$i);
            $manger->persist($task);
            $manger->flush();
        }

        $output->writeln($input->getOption("user_id") . "::create " . $input->getOption("count")." tasks");
    }
}