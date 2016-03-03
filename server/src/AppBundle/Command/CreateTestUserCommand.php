<?php
/**
 * Created by PhpStorm.
 * User: kamedon
 * Date: 3/3/16
 * Time: 2:46 PM
 */

namespace AppBundle\Command;


use AppBundle\Entity\ApiKey;
use Symfony\Bundle\FrameworkBundle\Command\ContainerAwareCommand;
use Symfony\Component\Console\Input\InputArgument;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Input\InputOption;
use Symfony\Component\Console\Output\OutputInterface;

class CreateTestUserCommand extends ContainerAwareCommand
{
    protected function configure()
    {
        $this
            ->setName('todo:user:create')
            ->setDescription('Create Test User')
            ->addArgument('name', InputArgument::OPTIONAL, 'username')
            ->addArgument('email', InputArgument::OPTIONAL, 'email')
            ->addArgument('password', InputArgument::OPTIONAL, 'password', 'password');
    }

    protected function execute(InputInterface $input, OutputInterface $output)
    {
        $password = $input->getArgument("password");
        $userManager = $this->getContainer()->get('fos_user.user_manager');
        $user = $userManager->createUser();
        $user->setEnabled(true);
        $user->setEmail($input->getArgument("email"));
        $user->setEmailCanonical($input->getArgument("email"));
        $user->setPlainPassword($password);
        $user->setUsername($input->getArgument("name"));

        if ($userManager->findUserByUsername($user->getUsername()) || $userManager->findUserByEmail($user->getEmail())) {
            $output->writeln('<error>すでに登録済みです</error>');
        }

        try {
            $userManager->updateUser($user);
        } catch (Exception $e) {
        }
        $key = \Ramsey\Uuid\Uuid::uuid1()->toString();
        $apiKey = new ApiKey();
        $apiKey->setUser($user);
        $apiKey->setToken($key);
        $em = $this->getContainer()->get("doctrine")->getManager();
        $em->persist($apiKey);
        $em->flush();

        $output->writeln("<info>created user: {$user->getUsername()} : {$password} : {$user->getEmail()}</info>");
        $output->writeln("<info>APiKey: {$apiKey->getToken()}</info>");
    }

}