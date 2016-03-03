<?php
namespace AppBundle\Service;

use Symfony\Bridge\Doctrine\RegistryInterface;
use Symfony\Component\DependencyInjection\Container;
use Symfony\Component\HttpFoundation\RequestStack;
use Symfony\Component\Security\Core\Exception\AccessDeniedException;

/**
 * アプリから呼ばれてるかチェックする
 */
class AuthRestService
{

    /**
     * @var RequestStack
     */
    private $request;
    /**
     * @var array
     */
    private $params;

    /** @var */
    private $registryInterface;

    /**
     * XUserAgentAuthorizationService constructor.
     * @param RequestStack $requestStack
     * @param RegistryInterface $registryInterface
     * @param array $params
     */
    public function __construct(RequestStack $requestStack, RegistryInterface $registryInterface, $params)
    {
        $this->requestStack = $requestStack;
        $this->params = $params;
        $this->registryInterface = $registryInterface;
    }

    public function auth()
    {
        if ($this->requestStack->getCurrentRequest()->headers->get($this->params[0]) !== md5($this->params[1])) {
            throw new AccessDeniedException("invalid request");
        }
    }

    public function authUser()
    {
        $request = $this->requestStack->getCurrentRequest();
        if ($request->headers->get($this->params[0]) !== md5($this->params[1])) {
            throw new AccessDeniedException("invalid request");
        }

        if (!$request->headers->has("Authorization")) {
            throw new AccessDeniedException("invalid request");
        }

        $repository = $this->registryInterface->getRepository("AppBundle:ApiKey");
        $apiKey = $repository->findOneBy(["token" => $request->headers->get("Authorization")]);
        if ($apiKey !== null) {
            return $apiKey->getUser();
        }
        throw new AccessDeniedException("invalid request");
    }

}