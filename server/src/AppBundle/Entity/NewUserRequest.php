<?php
/**
 * Created by PhpStorm.
 * User: kamedon
 * Date: 2/29/16
 * Time: 9:55 AM
 */

namespace AppBundle\Entity;
use Symfony\Component\Validator\Constraints as Assert;


class NewUserRequest
{

    /**
     * @Assert\NotBlank(
     *    message = "email should not be blank."
     * )
     * @Assert\Email()
     */
    protected $email;

    /**
     * @Assert\NotBlank(
     *    message = "password should not be blank."
     * )
     * @Assert\Length(
     *      min = 6,
     *      max = 16,
     *      minMessage = "password must be at least {{ limit }} characters long",
     *      maxMessage = "password cannot be longer than {{ limit }} characters"
     * )
     */
    protected $plainPassword;

    /**
     * @Assert\NotBlank(
     *    message = "name should not be blank."
     * )
     * @Assert\Length(
     *      min = 3,
     *      max = 16,
     *      minMessage = "name must be at least {{ limit }} characters long",
     *      maxMessage = "name cannot be longer than {{ limit }} characters"
     * )
     */
    protected $username;

    /**
     * @return mixed
     */
    public function getEmail()
    {
        return $this->email;
    }

    /**
     * @param mixed $email
     */
    public function setEmail($email)
    {
        $this->email = $email;
    }

    /**
     * @return mixed
     */
    public function getPlainPassword()
    {
        return $this->plainPassword;
    }

    /**
     * @param mixed $plainPassword
     */
    public function setPlainPassword($plainPassword)
    {
        $this->plainPassword = $plainPassword;
    }

    /**
     * @return mixed
     */
    public function getUsername()
    {
        return $this->username;
    }

    /**
     * @param mixed $username
     */
    public function setUsername($username)
    {
        $this->username = $username;
    }

}