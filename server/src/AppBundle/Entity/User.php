<?php
/**
 * Created by PhpStorm.
 * User: kamedon
 * Date: 2/25/16
 * Time: 10:54 PM
 */

namespace AppBundle\Entity;

use FOS\UserBundle\Model\User as BaseUser;
use Doctrine\ORM\Mapping as ORM;

/**
 * @ORM\Entity
 * @ORM\Table(name="fos_user")
 */
class User extends BaseUser
{
    /**
     * @ORM\Id
     * @ORM\Column(type="integer")
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    protected $id;

    /**
     * @ORM\OneToOne(targetEntity="ApiKey", mappedBy="user", cascade={"persist"})
     */
    protected $apiKey;

    /**
     * @ORM\OneToMany(targetEntity="Task", mappedBy="user")
     */
    protected $tasks;


    public function __construct()
    {
        parent::__construct();
        // your own logic
    }

    /**
     * Set apiKey
     *
     * @param \AppBundle\Entity\ApiKey $apiKey
     *
     * @return User
     */
    public function setApiKey(\AppBundle\Entity\ApiKey $apiKey = null)
    {
        $this->apiKey = $apiKey;

        return $this;
    }

    /**
     * Get apiKey
     *
     * @return \AppBundle\Entity\ApiKey
     */
    public function getApiKey()
    {
        return $this->apiKey;
    }

    /**
     * Add task
     *
     * @param \AppBundle\Entity\Task $task
     *
     * @return User
     */
    public function addTask(\AppBundle\Entity\Task $task)
    {
        $this->tasks[] = $task;

        return $this;
    }

    /**
     * Remove task
     *
     * @param \AppBundle\Entity\Task $task
     */
    public function removeTask(\AppBundle\Entity\Task $task)
    {
        $this->tasks->removeElement($task);
    }

    /**
     * Get tasks
     *
     * @return \Doctrine\Common\Collections\Collection
     */
    public function getTasks()
    {
        return $this->tasks;
    }
}
