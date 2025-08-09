package ml.espgm.gesem.services

import ml.espgm.gesem.entities.IpSecurityState

interface IpSecurityStateService {
    fun get(ip: String): IpSecurityState
    fun getOrCreate(ip: String): IpSecurityState
    fun markAsPermanentlyBlocked(ipState: IpSecurityState)
    fun promoteToOtp(ipState: IpSecurityState)
    fun promoteToLimited(ipState: IpSecurityState)
    fun isPermanentlyBlocked(ipState: IpSecurityState): Boolean
    fun isTemporarilyBlocked(ipState: IpSecurityState): Boolean
}