/*
 * Tpa2Member.java
 *
 * Created on September 29, 2007, 2:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.example.persistence;

/**
 *
 * @author peter
 */

import com.bbd.common.bbdenum.BbdEnums;
import com.bbd.common.bbdenum.BbdEnums.bbdContactType;
import com.bbd.common.bbdenum.BbdEnums.earningsFrequencyEnum;
import com.bbd.common.bbdenum.BbdEnums.FamilyCoverageOptionsEnum;
import com.bbd.common.bbdenum.workflow.NomadTypeEnums.changeTypesEnum;
import com.bbd.common.containers.MemberEnrolmentDataContainer;
import com.bbd.common.containers.nomad.workflow.NomadApprovalRequest;
import com.bbd.common.containers.nomad.workflow.NomadModelPropertyInfoEntry;
import com.bbd.common.containers.process.Tpa2EntityChangeEntries;
import com.bbd.common.entities.Tpa2LoggingEntity;
import com.bbd.common.entities.Tpa2MemberListEntry;
import com.bbd.common.entities.Tpa2MemberPartialWaiver;
import com.bbd.common.entities.Tpa2Validatable;
import com.bbd.common.process.utils.ModelPropertyInfoEntry;
import com.bbd.common.process.utils.ModelPropertyInfoList;
import com.bbd.common.process.utils.Tpa2MemberChangeProcessUtil;
import com.bbd.common.types.*;
import com.bbd.common.types.benefit.Tpa2BasicCoverageModel;
import com.bbd.common.types.benefit.Tpa2MemberCoverages;
import com.bbd.common.types.benefit.Tpa2StatusCoverageModel;
import com.bbd.common.types.complexid.Tpa2MemberId;
import com.bbd.common.utils.Tpa2LoggingEntityUtils;
import com.bbd.common.utils.Tpa2NumberUtils;
import com.bbd.common.utils.Tpa2SqlDateUtils;
import com.bbd.common.utils.Tpa2StringUtils;
import com.bbd.common.utils.tmp.EmployeeAbsoluteDates;
import com.bbd.common.xml.adapter.SQLDateAdapter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

@XmlRootElement( name = "member" )
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Inheritance
public class Tpa2Member extends Tpa2MemberListEntry implements Tpa2LoggingEntity, Cloneable, NomadApprovalRequest, Tpa2Validatable
{
    
    public Tpa2Member()
    {
        super();
    }
    
    /** Creates a new instance of Tpa2Member
     * @param memberId */
    public Tpa2Member( Tpa2MemberId memberId )
    {
        super( memberId );
    }

    /**
     * Sort of hack to allow client to set (different) ID after creation;
     * its use should be limited to only when adding a new member to the database
     * because ID is not known until after insertion
     * @param memberId
     */
    public void setId( Tpa2MemberId memberId )
    {
        complexId = memberId;
    }
    
    @Override
    @Transient
    public Tpa2MemberId getId()
    {
        return ( Tpa2MemberId ) super.getId();
    }

    @Id
    @GeneratedValue
    public Integer getEmployeeId() {
        Tpa2MemberId id = (Tpa2MemberId) super.getId();
        return id != null ? id.getEmployeeId() : null;
    }

    public void setEmployeeId(Integer employeeId) {
        setId(new Tpa2MemberId(employeeId));
    }
        
    @XmlJavaTypeAdapter(value=SQLDateAdapter.class)
    @XmlElement( name = EN_BIRTHDATE, nillable=true )
    public Date getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate( Date birthDate )
    {
        this.birthDate = birthDate;
    }

    @XmlElement( name = EN_AGE, nillable=true )
    public Integer getAge()
    {
        return age;
    }

    public void setAge( Integer age )
    {
        this.age = age;
    }

    @XmlElement( name = EN_MARITALSTATUS, nillable=true )
    @Transient
    public Integer getMaritalStatus()
    {
        return maritalStatus;
    }

    public void setMaritalStatus( Integer maritalStatus )
    {
        this.maritalStatus = maritalStatus;
    }

    @XmlJavaTypeAdapter(value=SQLDateAdapter.class)
    @XmlElement( name = EN_MARITAL_STATUS_EFF_DATE, nillable=true )
    @Transient
    public Date getMaritalStatusEffectiveDate()
    {
        return maritalStatusEffectiveDate;
    }

    public void setMaritalStatusEffectiveDate(Date cohabitationDate)
    {
        this.maritalStatusEffectiveDate = cohabitationDate;
    }

    /**
     * @return Province of residence (in case the contact address may be in another province)
     */
    @Transient
    public String getResidenceProvince()
    {
        return residenceProvince;
    }

    public void setResidenceProvince( String residenceProvince )
    {
        this.residenceProvince = residenceProvince;
    }

    @XmlElement( name = EN_ADDRESS, nillable=true )
    @Transient
    public bbdAddress getContactAddress()
    {
        if( contactAddress == null )
        {
            contactAddress = new bbdAddress();
        }
        return contactAddress;
    }

    public void setContactAddress( bbdAddress contactAddress )
    {
        this.contactAddress = contactAddress;
    }
    
    @XmlElement( name = EN_PHONE_NUMBER, nillable=true )
    @Transient
    public bbdPhoneContact getPhone()
    {
        return phone;
    }

    public void setPhone(bbdPhoneContact phone)
    {
        this.phone = phone;
    }

    @XmlElement( name = EN_CELL_NUMBER, nillable=true )
    @Transient
    public bbdPhoneContact getCellPhone()
    {
        return cellPhone;
    }

    public void setCellPhone(bbdPhoneContact cellPhone)
    {
        this.cellPhone = cellPhone;
        if (this.cellPhone != null)
        {
            // assign the correct type
            this.cellPhone.setContactType(bbdContactType.cell.getType());
        }
    }

    @XmlJavaTypeAdapter(value=SQLDateAdapter.class)
    @XmlElement( name = EN_EMPLOYMENTDATE, nillable=true )
    @Transient
    public Date getEmploymentDate()
    {
        return employmentDate;
    }

    public void setEmploymentDate( Date employmentDate )
    {
        this.employmentDate = employmentDate;
    }

    @XmlElement( name = EN_DEPTNUMBER, nillable=true )
    @Transient
    public String getDepartmentNumber()
    {
        return departmentNumber;
    }

    public void setDepartmentNumber( String departmentNumber )
    {
        this.departmentNumber = departmentNumber;
    }

    @XmlElement( name = EN_EMPNUMBER, nillable=true )
    @Transient
    public String getEmploymentNumber()
    {
        return employmentNumber;
    }

    public void setEmploymentNumber( String employmentNumber )
    {
        this.employmentNumber = employmentNumber;
    }

    @XmlElement( name = EN_WEEKLYHOURS, nillable=true )
    @Transient
    public Double getWeeklyWorkHours()
    {
        return weeklyWorkHours;
    }

    public void setWeeklyWorkHours( Double weeklyWorkHours )
    {
        this.weeklyWorkHours = weeklyWorkHours;
    }

    @XmlElement( name = EN_EARNINGS, nillable=true )
    @Transient
    public Tpa2Currency getEarnings()
    {
        return earnings;
    }

    public void setEarnings( Tpa2Currency earnings )
    {
        this.earnings = earnings;
    }

    @XmlElement( name = EN_EARNINGSFREQ, nillable=true )
    @Transient
    public Integer getEarningsFrequency()
    {
        return earningsFrequency;
    }

    /**
     * A helper method that calculates the annual earnings for an employee based
     * on his earnings frequency.
     *
     * @param m The employee whose earnings to calculate.
     * @return
     */
    @Transient
    public static Tpa2Currency getAnnualEarnings(Tpa2Member m)
    {
        Tpa2Currency annualEarnings = new Tpa2Currency();
        if (m.getEarnings() != null && m.getEarningsFrequency() != null)
        {
            if (m.getEarningsFrequency() ==
                earningsFrequencyEnum.earningsFrequencyHourly.value())
            {
                annualEarnings.add(m.getEarnings().toDouble() * 52 * m.getWeeklyWorkHours());
            }
            else if (m.getEarningsFrequency() ==
                earningsFrequencyEnum.earningsFrequencyBiWeekly.value())
            {
                annualEarnings.add(m.getEarnings().toDouble() * 26);
            }
            else if (m.getEarningsFrequency() ==
                earningsFrequencyEnum.earningsFrequencyWeekly.value())
            {
                annualEarnings.add(m.getEarnings().toDouble() * 52);
            }
            else if (m.getEarningsFrequency() ==
                earningsFrequencyEnum.earningsFrequencyMonthly.value())
            {
                annualEarnings.add(m.getEarnings().toDouble() * 12);
            }
            else if (m.getEarningsFrequency() ==
                earningsFrequencyEnum.earningsFrequencyAnnual.value())
            {
                annualEarnings.add(m.getEarnings().toDouble());
            }
        }
        return annualEarnings;
    }

    public void setEarningsFrequency( Integer earningsFrequency )
    {
        this.earningsFrequency = earningsFrequency;
    }

    @XmlElement( name = EN_OCC, nillable=true )
    @Transient
    public String getOccupation()
    {
        return occupation;
    }

    public void setOccupation( String occupation )
    {
        this.occupation = occupation;
    }

    /**
     * Sets a boolean value indicating that the group member's spouse has benefit coverage of his/her own.
     * @param spousalBenefitCoverage Boolean true if the group member's spouse has benefit coverage of his/her own.
     */
    public void setSpousalBenefitCoverage( Boolean spousalBenefitCoverage )
    {
        this.spousalBenefitCoverage = spousalBenefitCoverage;
    }

    /**
     * Returns TRUE if the group member's spouse has benefit coverage of his/her own.
     * @return True if the group member's spouse has benefit coverage of his/her own.
     */
    @Transient
    public Boolean getSpousalBenefitCoverage()
    {
        return spousalBenefitCoverage;
    }


    /**
     * If the Dental coverage is "Couple", does that mean COUPLE or SPOUSE?
     * @return Boolean TRUE if Dental coverage is SPOUSE, else FALSE.
     */
    @Transient
    public Boolean getCoupleDentalCoverIsSpousal()
    {
        return coupleDentalCoverIsSpousal;
    }


    /**
     * Set whether the Dental coverage being "Couple" actually means SPOUSAL
     * COVERAGE.
     * @param coupleDentalCoverIsSpousal Boolean TRUE if the couple Dental
     * Coverage is Spousal, else FALSE if COUPLE.
     */
    public void setCoupleDentalCoverIsSpousal( Boolean coupleDentalCoverIsSpousal )
    {
        this.coupleDentalCoverIsSpousal = coupleDentalCoverIsSpousal;
    }


    /**
     * If the Health coverage is "Couple", does that mean COUPLE or SPOUSE?
     * @return Boolean TRUE if Health coverage is SPOUSE, else FALSE.
     */
    @Transient
    public Boolean getCoupleHealthCoverIsSpousal()
    {
        return coupleHealthCoverIsSpousal;
    }


    /**
     * Set whether the Health coverage being "Couple" actually means SPOUSAL
     * COVERAGE.
     * @param coupleHealthCoverIsSpousal Boolean TRUE if the couple Health
     * Coverage is Spousal, else FALSE if COUPLE.
     */
    public void setCoupleHealthCoverIsSpousal( Boolean coupleHealthCoverIsSpousal )
    {
        this.coupleHealthCoverIsSpousal = coupleHealthCoverIsSpousal;
    }


    /**
     * Get the Group Member's BenAccount2.0 status (family/single/couple etc.)
     * @return The benAccount2Status - the Group Member's BenAccount2.0 status
     * (family/single/couple etc.).
     */
    @Transient
    public FamilyCoverageOptionsEnum getBenAccount2Status()
    {
        return benAccount2Status;
    }

    /**
     * Set the Group Member's BenAccount2.0 status (family/single/couple etc.).
     * @param benAccount2Status the benAccount2Status to set.
     */
    public void setBenAccount2Status( final FamilyCoverageOptionsEnum benAccount2Status )
    {
        this.benAccount2Status = benAccount2Status;
    }

    @Transient
    public Integer getBenAccount2Int()
    {
        Integer val = null;
        if( benAccount2Status != null)
        {
            val = benAccount2Status.value();
        }
        return val;
    }
    
    /**
     * Set the Group Member's BenAccount2.0 status (family/single/couple etc.).
     * @param benAccount2Status the benAccount2Status to set.
     */
    public void setBenAccount2Int( final Integer benAccount2Status )
    {
        if( benAccount2Status != null )
        {
            setBenAccount2Status( BbdEnums.getFamilyCoverageOptionsEnum( benAccount2Status ) );
        }
        else
        {
            setBenAccount2Status( BbdEnums.FamilyCoverageOptionsEnum.familyCoverageOptionsUnknown );
        }
    }    

    @XmlElement( name = EN_COVERAGES, nillable=true )
    @Transient
    public Tpa2MemberCoverages getCoverages()
    {
        return coverages;
    }

    public void setCoverages( Tpa2MemberCoverages coverages )
    {
        this.coverages = coverages;
    }

    /**
     * @return Overriding cap on Life Insurance benefit reduction
     * (eg. insurance company may put a cap on benefit reduction at age 65;
     * this is a desperate workaround when the cap has been introduced
     * too late after cases already exist that exceed their cap)
     */
    @Transient
    public Tpa2Currency getLifeBenReductionCapOverride()
    {
        return lifeBenReductionCapOverride;
    }

    public void setLifeBenReductionCapOverride( Tpa2Currency lifeBenReductionCapOverride )
    {
        this.lifeBenReductionCapOverride = lifeBenReductionCapOverride;
    }

    /**
     * @return Overriding cap on AD&D benefit reduction
     * (eg. insurance company may put a cap on benefit reduction at age 65;
     * this is a desperate workaround when the cap has been introduced
     * too late after cases already exist that exceed their cap)
     */
    @Transient
    public Tpa2Currency getAddBenReductionCapOverride()
    {
        return addBenReductionCapOverride;
    }

    public void setAddBenReductionCapOverride( Tpa2Currency addBenReductionCapOverride )
    {
        this.addBenReductionCapOverride = addBenReductionCapOverride;
    }


    /**
     * Get the member's DSAI status (Single or Couple/Family). If set (not null)
     * this value must override the member's normal DSAI status derived from
     * marital status.
     * @return the dsaiCoverageStatus - null if to be ignored, else Single (23),
     * Couple (24), Family (25).
     */
    @Transient
    public FamilyCoverageOptionsEnum getDsaiCoverageStatus()
    {
        // Attempt to calculate the DSAI coverage status first:
        // (The member coverage for DSAI should be the same as the DSAI status)
        if ( this.getCoverages() != null )
        {
            Tpa2BasicCoverageModel dsaiCover = getCoverages().getDSAICoverage();
            if ( dsaiCover != null && dsaiCover.getVolume() != null )
            {
                return BbdEnums.getFamilyCoverageOptionsEnum(( (Integer)dsaiCover.getVolume()));
            }
        }

        return dsaiCoverageStatus;

//        case 23:
//            return "Single";
//        case 24:
//            return "Couple";
//        case 25:
//            return "Family";
//        case 26:
//            return "Waived";
//        default:
//            return "Unknown";
    }

    /**
     * Get the member's MSP status (Single or Couple/Family). If set (not null)
     * this value must override the member's normal MSP status derived from
     * marital status.
     * @return the mspCoverageStatus - null if to be ignored, else Single (23),
     * Couple (24), Family (25).
     */
    @Transient
    public FamilyCoverageOptionsEnum getMspCoverageStatus()
    {
        // Attempt to calculate the MSP coverage status first:
        // (The member coverage for MSP should be the same as the MSP status)
        if ( this.getCoverages() != null )
        {
            Tpa2StatusCoverageModel mspCover = getCoverages().getMSPCoverage();
            if ( mspCover != null && mspCover.getStatus()!= null )
            {
                return BbdEnums.getFamilyCoverageOptionsEnum( mspCover.getStatus() );
            }
        }

        return mspCoverageStatus;
    }
    
    /**
     * @param dsaiCoverageStatus the dsaiCoverageStatus to set
     */
    public void setDsaiCoverageStatus( final FamilyCoverageOptionsEnum dsaiCoverageStatus )
    {
        this.dsaiCoverageStatus = dsaiCoverageStatus;
    }
    
    /**
     * @param mspCoverageStatus the mspCoverageStatus to set
     */
    public void setMspCoverageStatus( final FamilyCoverageOptionsEnum mspCoverageStatus )
    {
        this.mspCoverageStatus = mspCoverageStatus;
    }


    /**
     * Boolean value determining if the member is covered by WCB
     * @return Boolean value determining if the member is covered by WCB
     */
    @Transient
    public Boolean getNoWcbCoverage()
    {
        return wcbCoverage;
    }

    /**
     * Boolean value determining if the member is covered by WCB
     * @param wcbCoverage value determining if the member is covered by WCB
     */
    public void setNoWcbCoverage( Boolean wcbCoverage )
    {
        this.wcbCoverage = wcbCoverage;
    }

    /**
     * Sets the class value as displayed on the UI.
     * @param classDisplay
     * @see Tpa2Member#setClassID(java.lang.Integer) setClassID
     */
    public void setClassDisplay(String classDisplay)
    {
        this.classDisplay = classDisplay;
    }

    /**
     * Gets the class value as displayed on the UI.
     * @return classDisplay
     * @see Tpa2Member#getClassID() getClassID
     */
    @Transient
    public String getClassDisplay()
    {
        return classDisplay;
    }

    /**
     * The original employee id
     * @return
     */
    @Transient
    public Tpa2MemberId getOldEmployeeId()
    {
        return oldEmployeeId;
    }

    /**
     * Sets the original employee id
     * @param oldEmployeeId
     */
    public void setOldEmployeeId( Tpa2MemberId oldEmployeeId )
    {
        this.oldEmployeeId = oldEmployeeId;
    }

    /**
     * In case of member transfer to another group, this is employee_id of the member in the new group
     * @return
     */
    @Transient
    public Tpa2MemberId getNewEmployeeId()
    {
        return newEmployeeId;
    }

    public void setNewEmployeeId(Tpa2MemberId newEmployeeId)
    {
        this.newEmployeeId = newEmployeeId;
    }

    @Transient
    public String getSocialInsuranceNumber()
    {
        return socialInsuranceNumber;
    }

    public void setSocialInsuranceNumber(String socialInsuranceNumber)
    {
        this.socialInsuranceNumber = socialInsuranceNumber;
    }

    @XmlElement( name = "provHealthNumber", nillable=true )
    @Transient
    public String getProvHealthNumber()
    {
        return provHealthNumber;
    }

    public void setProvHealthNumber(String provHealthNumber)
    {
        this.provHealthNumber = provHealthNumber;
    }

    /**
     * Get the reason for the employee termination if the reason is in the "Other" category of termination reasons.
     * @return Reason for the employee termination.
     */
    @Transient
    public String getTerminationReason()
    {
        return terminationReason;
    }

    /**
     * Set the reason for the employee termination if the reason is in the "Other" category of termination reasons.
     * @param terminationReason Reason for the employee termination.
     */
    public void setTerminationReason( String terminationReason )
    {
        this.terminationReason = terminationReason;
    }

    @Transient
    public String getTerminationReasonId()
    {
        return terminationReasonId;
    }
    
    public void setTerminationReasonId(String terminationReasonId)
    {
        this.terminationReasonId = terminationReasonId;
    }

    @Transient
    public Integer getSurvivorLinkToMemberId()
    {
        return survivorLinkToMemberId;
    }

    public void setSurvivorLinkToMemberId(Integer survivorLinkToMemberId)
    {
        this.survivorLinkToMemberId = survivorLinkToMemberId;
    }

    @XmlElement( name = "enrollment", nillable=true )
    @Transient
    public MemberEnrolmentDataContainer getHealthEnrolment()
    {
        return healthEnrolment;
    }

    public void setHealthEnrolment(MemberEnrolmentDataContainer healthEnrolment)
    {
        this.healthEnrolment = healthEnrolment;
    }

    @Transient
    public MemberEnrolmentDataContainer getHcsaEnrolment() 
    {
        return hcsaEnrolment;
    }

    public void setHcsaEnrolment(MemberEnrolmentDataContainer hcsaEnrolment) 
    {
        this.hcsaEnrolment = hcsaEnrolment;
    }

    /**
     * @return the psaEnrolment
     */
    @Transient
    public MemberEnrolmentDataContainer getPsaEnrolment()
    {
        return psaEnrolment;
    }

    /**
     * @param psaEnrolment the psaEnrolment to set
     */
    public void setPsaEnrolment(MemberEnrolmentDataContainer psaEnrolment)
    {
        this.psaEnrolment = psaEnrolment;
    }

    @Transient
    public MemberEnrolmentDataContainer getDentalEnrolment()
    {
        return dentalEnrolment;
    }

    public void setDentalEnrolment(MemberEnrolmentDataContainer dentalEnrolment)
    {
        this.dentalEnrolment = dentalEnrolment;
    }

    @Transient
    public Tpa2Currency getGsiRate()
    {
        return gsiRate;
    }

    public void setGsiRate(Tpa2Currency gsiRate)
    {
        this.gsiRate = gsiRate;
    }

    @XmlElement( name = EN_EMAIL, nillable=true )
    @Transient
    public bbdContact getEmailAddress()
    {
        if( emailAddress == null )
        {
            emailAddress = new bbdContact();
            emailAddress.setContactType( BbdEnums.bbdContactType.email.getType() );
        }
        return emailAddress;
    }

    public void setEmailAddress( bbdContact emailAddress )
    {
        this.emailAddress = emailAddress;
    }

    public void setEmailAddress(String emailAddressStr)
    {
        setEmailAddress(emailAddressStr, BbdEnums.bbdContactType.email);
    }
    
    public void setEmailAddress(String emailAddressStr, BbdEnums.bbdContactType type)
    {
        if (BbdEnums.bbdContactType.email.equals(type))
        {
            if (emailAddress == null)
            {
                emailAddress = new bbdContact();
                emailAddress.setContactType(BbdEnums.bbdContactType.email.getType());
            }
            emailAddress.setContactInfo(emailAddressStr);
        }
        else if (BbdEnums.bbdContactType.emailAlt.equals(type))
        {
            if (emailAltAddress == null)
            {
                emailAltAddress = new bbdContact();
                emailAltAddress.setContactType(BbdEnums.bbdContactType.emailAlt.getType());
            }
            emailAltAddress.setContactInfo(emailAddressStr);
        }
  }

    @XmlElement( name = EN_ALT_EMAIL, nillable=true )
    @Transient
    public bbdContact getEmailAltAddress()
    {
        return emailAltAddress;
    }

    public void setEmailAltAddress(bbdContact emailAltAddress)
    {
        this.emailAltAddress = emailAltAddress;
        if( this.emailAltAddress != null )
        {
            emailAltAddress.setContactType( BbdEnums.bbdContactType.emailAlt.getType() );
        }
    }
    
    public void setEmailAltAddress(String emailAltAddress)
    {
        setEmailAddress(emailAltAddress, BbdEnums.bbdContactType.emailAlt);
    }

    /**
     * @return the mspCoverage
     */
    @XmlElement( name = EN_MSP, nillable=true )
    @Transient
    public Boolean getMspCoverage() {
        return mspCoverage;
    }

    /**
     * @param mspCoverage the mspCoverage to set
     */
    public void setMspCoverage( Boolean mspCoverage ) {
        this.mspCoverage = mspCoverage;
    }

    @XmlJavaTypeAdapter(value=SQLDateAdapter.class)
    @XmlElement( name = EN_WORKVISA, nillable=true )
    @Transient
    public Date getWorkVisaValidUntilDate()
    {
        return workVisaValidUntilDate;
    }
    
    public void setWorkVisaValidUntilDate( Date workVisaValidUntilDate )
    {
        this.workVisaValidUntilDate = workVisaValidUntilDate;
    }
    

    /**
     * @return the nameChangeReason
     */
    @XmlElement( name = "nameChangeReason", nillable=true )
    @Transient
    public Integer getNameChangeReason()
    {
        return nameChangeReason;
    }
    
    /**
     * @param nameChangeReason the nameChangeReason to set
     */
    public void setNameChangeReason( Integer nameChangeReason )
    {
        this.nameChangeReason = nameChangeReason;
    }    
    
    /**
     * @return the nameChangeReasonOther
     */
    @XmlElement( name = "nameChangeReasonOther", nillable=true )
    @Transient
    public String getNameChangeReasonOther()
    {
        return nameChangeReasonOther;
    }
    
    /**
     * @param nameChangeReasonOther the nameChangeReasonOther to set
     */
    public void setNameChangeReasonOther( String nameChangeReasonOther )
    {
        this.nameChangeReasonOther = nameChangeReasonOther;
    }    
    
    /**
     * @return the waiveWaitingPeriodReasonDesc
     */
    @XmlElement( name = "waiveWaitingPeriodReasonDesc", nillable=true )
    @Transient
    public String getWaiveWaitingPeriodReasonDesc()
    {
        return waiveWaitingPeriodReasonDesc;
    }
    
    /**
     * @param waiveWaitingPeriodReasonDesc the waiveWaitingPeriodReasonDesc to set
     */
    public void setWaiveWaitingPeriodReasonDesc( String waiveWaitingPeriodReasonDesc )
    {
        this.waiveWaitingPeriodReasonDesc = waiveWaitingPeriodReasonDesc;
    }    
    
    /**
     * Get the value of expatriateCoverageStartDate
     *
     * @return the value of expatriateCoverageStartDate
     */
    @Transient
    public Date getExpatriateCoverageStartDate()
    {
        return expatriateCoverageStartDate;
    }

    /**
     * Set the value of expatriateCoverageStartDate
     *
     * @param expatriateCoverageStartDate new value of expatriateCoverageStartDate
     */
    public void setExpatriateCoverageStartDate(Date expatriateCoverageStartDate)
    {
        this.expatriateCoverageStartDate = expatriateCoverageStartDate;
    }


    /**
     * Get the value of expatriateHostCountry
     *
     * @return the value of expatriateHostCountry
     */
    @Transient
    public String getExpatriateHostCountry()
    {
        return expatriateHostCountry;
    }

    /**
     * Set the value of expatriateHostCountry
     *
     * @param expatriateHostCountry new value of expatriateHostCountry
     */
    public void setExpatriateHostCountry(String expatriateHostCountry)
    {
        this.expatriateHostCountry = expatriateHostCountry;
    }


    /**
     * Get the value of expatriateCountryOfCitizenship
     *
     * @return the value of expatriateCountryOfCitizenship
     */
    @Transient
    public String getExpatriateCountryOfCitizenship()
    {
        return expatriateCountryOfCitizenship;
    }

    /**
     * Set the value of expatriateCountryOfCitizenship
     *
     * @param expatriateCountryOfCitizenship new value of expatriateCountryOfCitizenship
     */
    public void setExpatriateCountryOfCitizenship(String expatriateCountryOfCitizenship)
    {
        this.expatriateCountryOfCitizenship = expatriateCountryOfCitizenship;
    }


    /**
     * Get the value of expatriate
     *
     * @return the value of expatriate
     */
    @Transient
    public boolean isExpatriate()
    {
        return expatriate;
    }

    /**
     * Set the value of expatriate
     *
     * @param expatriate new value of expatriate
     */
    public void setExpatriate(boolean expatriate)
    {
        this.expatriate = expatriate;
    }


    /**
     * @return the absoluteDatesHolder
     */
    @Transient
    public EmployeeAbsoluteDates getAbsoluteDatesHolder()
    {
        return absoluteDatesHolder;
    }

    /**
     * @param absoluteDatesHolder the absoluteDatesHolder to set
     */
    public void setAbsoluteDatesHolder( EmployeeAbsoluteDates absoluteDatesHolder )
    {
        this.absoluteDatesHolder = absoluteDatesHolder;
    }


    @Override
    @Transient
    public String getEntityIdForLogEntry()
    {
        return String.valueOf( getGroupID() );
    }

    @Override
    @Transient
    public String getEntityNameForLogEntry()
    {
        return super.getEntityNameForLogEntry();
    }

    @Override
    @Transient
    public String getChangesForLogEntry( Tpa2LoggingEntity originalEntity )
    {
        Tpa2Member original = ( Tpa2Member ) originalEntity;

        if ( original == null )
        {
            original = new Tpa2Member( null );
        }

        String updMaritalStatus = null;
        String origMaritalStatus = null;

        if ( Tpa2NumberUtils.getInt( getMaritalStatus() ) != -1 )
        {
            updMaritalStatus = BbdEnums.getBbdMaritalStatusEnum( Tpa2NumberUtils.getInt( getMaritalStatus() ) ).toString();
        }

        if ( Tpa2NumberUtils.getInt( original.getMaritalStatus() ) != -1 )
        {
            origMaritalStatus = BbdEnums.getBbdMaritalStatusEnum( Tpa2NumberUtils.getInt( original.getMaritalStatus() ) ).toString();
        }

        String updEarningsFreq = null;
        String origEarningsFreq = null;

        if ( Tpa2NumberUtils.getInt( getEarningsFrequency() ) != -1 )
        {
            updEarningsFreq = BbdEnums.getEarningsFrequencyEnum( Tpa2NumberUtils.getInt( getEarningsFrequency() ) ).toString();
        }

        if ( Tpa2NumberUtils.getInt( original.getEarningsFrequency() ) != -1 )
        {
            origEarningsFreq = BbdEnums.getEarningsFrequencyEnum( Tpa2NumberUtils.getInt( original.getEarningsFrequency() ) ).toString();
        }

        String updOccupationClass = null;
        String origOccupationClass = null;

        if ( Tpa2NumberUtils.getInt( getOccupationClass() ) != -1 )
        {
            updOccupationClass = Tpa2NumberUtils.getLeadingZeroNumber( getOccupationClass() ) + " " + BbdEnums.getBbdOccupationClass( Tpa2NumberUtils.getInt( getOccupationClass() ) ).toString();
        }

        if ( Tpa2NumberUtils.getInt( original.getOccupationClass() ) != -1 )
        {
            origOccupationClass = Tpa2NumberUtils.getLeadingZeroNumber( original.getOccupationClass() ) + " " + BbdEnums.getBbdOccupationClass( Tpa2NumberUtils.getInt( original.getOccupationClass() ) ).toString();
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append( super.getChangesForLogEntry( originalEntity ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Termination Reason", getTerminationReason(), original.getTerminationReason() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Date of Birth", getBirthDate(), original.getBirthDate() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Age", getAge(), original.getAge() ));
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Province", getResidenceProvince(), original.getResidenceProvince() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Marital Status", updMaritalStatus, origMaritalStatus ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Marital Status Effective Date", getMaritalStatusEffectiveDate(), original.getMaritalStatusEffectiveDate() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Employment Date", getEmploymentDate(), original.getEmploymentDate() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Department Number", getDepartmentNumber(), original.getDepartmentNumber() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Employment Number", getEmploymentNumber(), original.getEmploymentNumber() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Weekly Hours", getWeeklyWorkHours(), original.getWeeklyWorkHours() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Earnings", getEarnings(), original.getEarnings() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Earnings Frequency", updEarningsFreq, origEarningsFreq ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Occupation", getOccupation(), original.getOccupation() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Occupation Class", updOccupationClass, origOccupationClass ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Auto-Coordination", getSpousalBenefitCoverage(), original.getSpousalBenefitCoverage() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Life Benefit Reduction Cap Override", getLifeBenReductionCapOverride(), original.getLifeBenReductionCapOverride() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "ADD Benefit Reduction Cap Override", getAddBenReductionCapOverride(), original.getAddBenReductionCapOverride() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Class", getClassDisplay(), original.getClassDisplay() ));
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Link to this Employee", getSurvivorLinkToMemberId(), original.getSurvivorLinkToMemberId() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "CI GSI Rate", getGsiRate(), original.getGsiRate() ) );

        // BenAccount2 status, Health & Dental statusus
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Couple Dental Coverage is for Spouse? ", getCoupleDentalCoverIsSpousal(), original.getCoupleDentalCoverIsSpousal() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Couple EHB Coverage is for Spouse? ", getCoupleHealthCoverIsSpousal(), original.getCoupleHealthCoverIsSpousal() ) );

        if ( getBenAccount2Status() != null || original.getBenAccount2Status() != null )
        {
            String benAccStatusStr = ( getBenAccount2Status() != null  ) ? getBenAccount2Status().toString() : "Unknown";
            String origBenAccStatusStr = ( original.getBenAccount2Status() != null  ) ? original.getBenAccount2Status().toString() : "Unknown";
            buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "BenAccount 2.0 Status", benAccStatusStr, origBenAccStatusStr ) );
        }

        if( getContactAddress() != null )
        {
            buffer.append( getContactAddress().getChangesForLogEntry( original.getContactAddress() ) );
        }

        if( getEmailAddress() != null )
        {
            buffer.append( getEmailAddress().getChangesForLogEntry( original.getEmailAddress() ) );
        }

        if( getCoverages() != null )
        {
            buffer.append( getCoverages().getChangesForLogEntry( original.getCoverages() ) );
        }

        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "No WCB", getNoWcbCoverage(), original.getNoWcbCoverage() ) );
        buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage( "Work Visa Valid Until", getWorkVisaValidUntilDate(), original.getWorkVisaValidUntilDate() ) );
        
        if (getPartialWaiver() != null)
        {
            Tpa2MemberPartialWaiver originalPartialWaiver = original.getPartialWaiver();
            buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage(
                    "Partial Waivers - Checkbox",
                    getPartialWaiver().getSpousalPlan(),
                    originalPartialWaiver == null ? null : originalPartialWaiver.getSpousalPlan() ) );
            buffer.append(getPartialWaiver().getChangesForLogEntry(originalPartialWaiver));
        }
        else if (original.getPartialWaiver() != null)
        {
            Tpa2MemberPartialWaiver originalPartialWaiver = original.getPartialWaiver();
            buffer.append( Tpa2LoggingEntityUtils.getChangeLogMessage(
                    "Partial Waivers - Checkbox", null,
                    originalPartialWaiver == null ? null : originalPartialWaiver.getSpousalPlan() ) );
            buffer.append(new Tpa2MemberPartialWaiver().getChangesForLogEntry(originalPartialWaiver));
        }
        

        if (getDentalEnrolment() != null)
        {
            buffer.append(getDentalEnrolment().getChangesForLogEntry(original.getDentalEnrolment()));
        }

        if (getHealthEnrolment() != null)
        {
            buffer.append(getHealthEnrolment().getChangesForLogEntry(original.getHealthEnrolment()));
        }
        
        if (getHcsaEnrolment() != null)
        {
            buffer.append(getHcsaEnrolment().getChangesForLogEntry(original.getHcsaEnrolment()));
        }

        buffer.append(Tpa2LoggingEntityUtils
            .getChangeLogMessage("Provincial Health Care Number",
            getProvHealthNumber(), original.getProvHealthNumber()));

        buffer.append(Tpa2LoggingEntityUtils
            .getChangeLogMessage("Smoker", getSmoker(), original.getSmoker()));
        
        return buffer.toString();
    }

    @Override
    @Transient
    public String getDeleteLogEntry()
    {
        return super.getDeleteLogEntry();
    }

    public void copy( Tpa2Member updated )
    {
        copy( updated, true, false );
    }

    public void copy( Tpa2Member updated, boolean copyBlankValues )
    {
        copy( updated, copyBlankValues, false );
    }
    
    public void copy( Tpa2Member updated, boolean copyBlankValues, boolean exactClone )
    {
        if( exactClone || !Tpa2StringUtils.isEmpty( updated.getNameFirst() ) )
        {
            setNameFirst( updated.getNameFirst() );
        }
        if( exactClone || !Tpa2StringUtils.isEmpty( updated.getNameInitial() ) )
        {
            setNameInitial( updated.getNameInitial() );
        }
        if( exactClone || !Tpa2StringUtils.isEmpty( updated.getNameLast() ) )
        {
            setNameLast( updated.getNameLast() );
        }
        if( updated.getClassID() != null )
        {
            setClassID(updated.getClassID());
        }
        if( updated.getSex() != null )
        {
            setSex(updated.getSex());
        }
        if( exactClone || !Tpa2StringUtils.isEmpty( updated.getPreferredLanguage() ) )
        {
            setPreferredLanguage( updated.getPreferredLanguage() );
        }
        if( updated.getBirthDate() != null )
        {
            setBirthDate( Tpa2SqlDateUtils.getSqlDate(updated.getBirthDate()) );
        }
        if( updated.getMaritalStatus() != null )
        {
            setMaritalStatus(updated.getMaritalStatus());
        }
        if (updated.getMaritalStatusEffectiveDate() != null)
        {
            setMaritalStatusEffectiveDate(Tpa2SqlDateUtils.getSqlDate(updated.getMaritalStatusEffectiveDate()));
        }
        if( exactClone || !Tpa2StringUtils.isEmpty( updated.getResidenceProvince() ) )
        {
            setResidenceProvince( updated.getResidenceProvince() );
        }
        if( updated.getContactAddress() != null )
        {
            bbdAddress address = new bbdAddress();
            address.copy(updated.getContactAddress());
            setContactAddress( address );
        }
        if( updated.getEmploymentDate() != null )
        {
            setEmploymentDate( Tpa2SqlDateUtils.getSqlDate(updated.getEmploymentDate()) );
        }
        if( exactClone || !Tpa2StringUtils.isEmpty( updated.getDepartmentNumber() ) )
        {
            setDepartmentNumber( updated.getDepartmentNumber() );
        }
        if( exactClone || !Tpa2StringUtils.isEmpty( updated.getEmploymentNumber() ) )
        {
            setEmploymentNumber( updated.getEmploymentNumber() );
        }
        if( updated.getWeeklyWorkHours() != null )
        {
            setWeeklyWorkHours(updated.getWeeklyWorkHours());
        }
        if( updated.getEarnings() != null )
        {
            setEarnings( new Tpa2Currency(updated.getEarnings().toDouble()) );
        }
        if( updated.getEarningsFrequency() != null )
        {
            setEarningsFrequency(updated.getEarningsFrequency());
        }
        if( exactClone || !Tpa2StringUtils.isEmpty( updated.getOccupation() ) )
        {
            setOccupation( updated.getOccupation() );
        }
        if( updated.getOccupationClass() != null )
        {
            setOccupationClass(updated.getOccupationClass());
        }
        if( updated.getSpousalBenefitCoverage() != null )
        {
            setSpousalBenefitCoverage(updated.getSpousalBenefitCoverage());
        }
        if( updated.getCoverages() != null )
        {
            Tpa2MemberCoverages newCoverages = new Tpa2MemberCoverages();
            newCoverages.copy(updated.getCoverages());
            setCoverages( newCoverages );
        }
        if( updated.getLifeBenReductionCapOverride() != null )
        {
            setLifeBenReductionCapOverride( new Tpa2Currency(updated.getLifeBenReductionCapOverride().toDouble()) );
        }
        if( updated.getAddBenReductionCapOverride() != null )
        {
            setAddBenReductionCapOverride( new Tpa2Currency(updated.getAddBenReductionCapOverride().toDouble()) );
        }
        if( updated.getNoWcbCoverage() != null )
        {
            setNoWcbCoverage(updated.getNoWcbCoverage());
        }
       if( updated.getMspCoverage() != null )
        {
            setMspCoverage(updated.getMspCoverage());
        }
        if (updated.getClassDisplay() != null)
        {
            setClassDisplay(updated.getClassDisplay());
        }
        if (updated.getOldEmployeeId() != null)
        {
            Tpa2MemberId oldMemberId = new Tpa2MemberId(updated.getOldEmployeeId());
            setOldEmployeeId( oldMemberId );
        }
        if (updated.getNewEmployeeId() != null)
        {
            Tpa2MemberId newMemberId = new Tpa2MemberId(updated.getNewEmployeeId());
            setNewEmployeeId( newMemberId );
        }
        if( updated.getAge() != null )
        {
            setAge(updated.getAge());
        }
        if ( updated.getDsaiCoverageStatus() != null )
        {
            setDsaiCoverageStatus( updated.getDsaiCoverageStatus() );
        }
        if ( updated.getMspCoverageStatus() != null )
        {
            setMspCoverageStatus( updated.getMspCoverageStatus() );
        }
        if( updated.getSurvivorLinkToMemberId() != null )
        {
            setSurvivorLinkToMemberId(updated.getSurvivorLinkToMemberId());
        }
        if (updated.getGsiRate() != null)
        {
            setGsiRate(new Tpa2Currency(updated.getGsiRate().toDouble()));
        }
        if(  updated.getEmailAddress() != null )
        {
            bbdContact email = new bbdContact();
            email.setContactInfo( updated.getEmailAddress().getContactInfo() );
            email.setContactType( updated.getEmailAddress().getContactType() );
            setEmailAddress( email );
        }
        if( updated.getEmailAltAddress()!= null )
        {
            bbdContact email = new bbdContact();
            email.setContactInfo( updated.getEmailAltAddress().getContactInfo() );
            email.setContactType( updated.getEmailAltAddress().getContactType() );
            setEmailAltAddress( email );
        }
        if( updated.getPhone() != null )
        {
            setPhone( updated.getPhone() );
        }
        if( updated.getCellPhone()!= null )
        {
            setCellPhone( updated.getCellPhone() );
        }

        // BenAccount2 status, Health & Dental statusus
        setBenAccount2Status( updated.getBenAccount2Status() );
        setCoupleDentalCoverIsSpousal( updated.getCoupleDentalCoverIsSpousal() );
        setCoupleHealthCoverIsSpousal( updated.getCoupleHealthCoverIsSpousal() );

        if (updated.getTerminationDate() != null)
        {
            setTerminationDate( Tpa2SqlDateUtils.getSqlDate(updated.getTerminationDate()) );
        }

        if (updated.getEmploymentTerminationDate() != null)
        {
            setEmploymentTerminationDate(Tpa2SqlDateUtils.getSqlDate(updated.getEmploymentTerminationDate()));
        }
        
        if( exactClone || !copyBlankValues )
        {            
            if (updated.getReinstateDate() != null)
            {
                setReinstateDate( Tpa2SqlDateUtils.getSqlDate(updated.getReinstateDate()) );
            }
        }

        if (exactClone || !Tpa2StringUtils.isEmpty(updated.getTerminationReason()))
        {
            setTerminationReason(updated.getTerminationReason());
        }

        if (updated.getRehireDate() != null)
        {
            setRehireDate(Tpa2SqlDateUtils.getSqlDate(updated.getRehireDate()));
        }
        
        if( updated.getWorkVisaValidUntilDate() != null )
        {
            setWorkVisaValidUntilDate( updated.getWorkVisaValidUntilDate() );
        }
        
        setExpatriate( updated.isExpatriate() );
        
        if ( updated.getExpatriateCountryOfCitizenship() != null )
        {
            setExpatriateCountryOfCitizenship( updated.getExpatriateCountryOfCitizenship() );
        } 
        
        if ( updated.getExpatriateHostCountry() != null )
        {
            setExpatriateHostCountry( updated.getExpatriateHostCountry() );
        }
        
        if ( updated.getExpatriateCoverageStartDate() != null )
        {
            setExpatriateCoverageStartDate( updated.getExpatriateCoverageStartDate() );
        }

        if (updated.getMaritalStatusEffectiveDate() != null)
        {
            setMaritalStatusEffectiveDate(updated.getMaritalStatusEffectiveDate());
        }

        if (updated.getPartialWaiver() != null)
        {
            setPartialWaiver(updated.getPartialWaiver());
        }

        if (updated.getDentalEnrolment() != null)
        {
            setDentalEnrolment(updated.getDentalEnrolment());
        }

        if (updated.getHealthEnrolment() != null)
        {
            setHealthEnrolment(updated.getHealthEnrolment());
        }
        
        if (updated.getHcsaEnrolment()!= null)
        {
            setHcsaEnrolment(updated.getHcsaEnrolment());
        }
        
        if( updated.getWaiveWaitingPeriodReasonDesc() != null )
        {
            setWaiveWaitingPeriodReasonDesc( updated.getWaiveWaitingPeriodReasonDesc() );
        }

        if (updated.getProvHealthNumber() != null)
        {
            setProvHealthNumber(updated.getProvHealthNumber());
        }
        setSmoker(updated.getSmoker());
    }
    
    /*@Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }*/
    
    @Override
    public boolean isApprovalRequired( String methodName )
    {
        return NOMAD_METHOD_FIELD_LABEL_MAP.containsKey( methodName );
    }

    @Override
    public NomadModelPropertyInfoEntry getNomadPropertyInfoEntry( String methodName )
    {
        return NOMAD_METHOD_FIELD_LABEL_MAP.get( methodName );
    }

    @Override
    @Transient
    public changeTypesEnum getChangeTypeEnum()
    {
        return changeTypesEnum.changeTypesEmployee;
    }

    @Override
    public Boolean isProcessFieldAffectVolPrem(String fieldName)
    {
        return PROCESS_FIELD_LIST.isPropertyAffectVolPrem(fieldName); 
    }
    
    @Override
    public Boolean isProcessField(String fieldName)
    {
        return PROCESS_FIELD_LIST.isPropertyExist(fieldName);
    }
    
    @Override
    public String getProcessFieldDisplayName(String fieldName)
    {
        String labelStr = super.getProcessFieldDisplayName(fieldName);
        if (labelStr != null)
        {
            return labelStr;
        }
        else
        {
            return PROCESS_FIELD_LIST.getPropertyDisplayName(fieldName);
        }
    }
    
    @Override
    public String getProcessFieldEnum(String fieldName)
    {
        String labelStr = super.getProcessFieldEnum(fieldName);
        if (labelStr != null)
        {
            return labelStr;
        }
        else
        {
            return PROCESS_FIELD_LIST.getPropertyEnum(fieldName);
        }
    }

    @Override
    public Boolean isRequiredForValidation( String methodName )
    {
        return VALIDATION_REQUIRED_FIELDS_MAP.containsKey( methodName );
    }

    @Override
    public ModelPropertyInfoEntry getValidatedElementName( String methodName )
    {
        return VALIDATION_REQUIRED_FIELDS_MAP.get( methodName );
    }        
    
    public Tpa2EntityChangeEntries getChangeEntries(Tpa2Member updated)
    {
        Tpa2EntityChangeEntries memberChanges = new Tpa2EntityChangeEntries();
        
        super.generateChangeEntries(updated, memberChanges, null);
        
        Tpa2MemberChangeProcessUtil.processChangeEntries(this, updated, memberChanges, Tpa2Member.class);

        if (getCoverages() != null)
        {
            getCoverages().generateChangedEntries(updated.getCoverages(), memberChanges, null);
        }
        
        if (getContactAddress() != null)
        {
            getContactAddress().generateChangeEntries(updated.getContactAddress(), memberChanges, null);
        }

        if (getEmailAddress() != null)
        {
            getEmailAddress().generateChangeEntries(updated.getEmailAddress(), memberChanges, null);
        }

        if (getEmailAltAddress() != null)
        {
            getEmailAltAddress().generateChangeEntries(updated.getEmailAltAddress(), memberChanges, null);
        }

        if (getPhone() != null)
        {
            getPhone().generateChangeEntries(updated.getPhone(), memberChanges, null);
        }

        if (getCellPhone() != null)
        {
            getCellPhone().generateChangeEntries(updated.getCellPhone(), memberChanges, null);
        }

        return memberChanges;
    }

    @XmlElement( name = "partialWaiver", nillable=true )
    @Transient
    public Tpa2MemberPartialWaiver getPartialWaiver() 
    {
        return partialWaiver;
    }

    public void setPartialWaiver(Tpa2MemberPartialWaiver partialWaiver) 
    {
        this.partialWaiver = partialWaiver;
    }
    
    /**
     * Gets the (database) reference to the member's bank account data. This will be needed to
     * retrieve the member's banking information.
     * @return the bankAccountDataId as a Tpa2Guid object.
     */
    @Transient
    public Tpa2Guid getBankAccountDataId()
    {
        return bankAccountDataId;
    }

    /**
     * Sets the (database) reference to the member's bank account data. This will be needed to
     * retrieve the member's banking information.
     * @param bankAccountDataId the bankAccountDataId to set.
     */
    public void setBankAccountDataId( Tpa2Guid bankAccountDataId )
    {
        this.bankAccountDataId = bankAccountDataId;
    }

    @XmlElement( name = EN_SMOKER, nillable=true )
    @Transient
    public Boolean getSmoker()
    {
        return smoker;
    }

    public void setSmoker(Boolean smoker)
    {
        this.smoker = smoker;
    }

    @Transient
    public Boolean getBeneficiariesSigned()
    {
        return beneficiariesSigned;
    }

    public void setBeneficiariesSigned(Boolean beneficiariesSigned)
    {
        this.beneficiariesSigned = beneficiariesSigned;
    }

    private Date birthDate;
    private Integer maritalStatus;
    private String residenceProvince;
    private bbdAddress contactAddress;
    private bbdContact emailAddress;
    private bbdContact emailAltAddress;
    private bbdPhoneContact phone;
    private bbdPhoneContact cellPhone;
    private Date employmentDate;
    private String departmentNumber;
    private String employmentNumber;
    private Double weeklyWorkHours;
    private Tpa2Currency earnings;
    private Integer earningsFrequency;
    private String occupation;
    private Boolean spousalBenefitCoverage;
    private Boolean coupleDentalCoverIsSpousal;
    private Boolean coupleHealthCoverIsSpousal;
    private Tpa2MemberCoverages coverages;
    private Tpa2Currency lifeBenReductionCapOverride;
    private Tpa2Currency addBenReductionCapOverride;
    private FamilyCoverageOptionsEnum dsaiCoverageStatus;
    private FamilyCoverageOptionsEnum mspCoverageStatus;
    private FamilyCoverageOptionsEnum benAccount2Status;
    private Boolean wcbCoverage;
    private Boolean mspCoverage;
    private String classDisplay;
    private Tpa2MemberId oldEmployeeId;
    private Tpa2MemberId newEmployeeId;
    private Integer age;
    private String socialInsuranceNumber = "000000000";
    private String terminationReason;
    private String terminationReasonId;
    private Integer survivorLinkToMemberId;
    private Tpa2Currency gsiRate;
    private Date workVisaValidUntilDate;
    private Date maritalStatusEffectiveDate;
    private Integer nameChangeReason;
    private String nameChangeReasonOther;
    private String waiveWaitingPeriodReasonDesc;    
    private boolean expatriate;    
    private String expatriateCountryOfCitizenship;    
    private String expatriateHostCountry;    
    private Date expatriateCoverageStartDate;
    private String provHealthNumber;
    private Tpa2Guid bankAccountDataId;
    private EmployeeAbsoluteDates absoluteDatesHolder;
    private Boolean smoker = Boolean.FALSE;
    private Boolean beneficiariesSigned;

    
    private MemberEnrolmentDataContainer healthEnrolment;
    private MemberEnrolmentDataContainer dentalEnrolment;
    private MemberEnrolmentDataContainer hcsaEnrolment;
    private MemberEnrolmentDataContainer psaEnrolment;
    private Tpa2MemberPartialWaiver partialWaiver;

    private static final Map<String, NomadModelPropertyInfoEntry> NOMAD_METHOD_FIELD_LABEL_MAP;
    private static final ModelPropertyInfoList PROCESS_FIELD_LIST;
    private static final Map<String, NomadModelPropertyInfoEntry> VALIDATION_REQUIRED_FIELDS_MAP;

    // EN = Element Name (Please try and match this value as to what is on the screen.
    // Do NOT change any existing values as it will have a major impact with the NOMAD
    // Client.  These values are used for validating fields
    public static final String EN_BIRTHDATE = "birthDate";
    public static final String EN_AGE = "age";
    public static final String EN_MARITALSTATUS = "maritalStatus";
    public static final String EN_ADDRESS = "address";
    public static final String EN_PHONE_NUMBER = "phoneNumber";
    public static final String EN_CELL_NUMBER = "cellNumber";
    public static final String EN_ALT_EMAIL = "alternateEmail";
    public static final String EN_EMPLOYMENTDATE = "employmentDate";
    public static final String EN_DEPTNUMBER = "departmentNumber";
    public static final String EN_EMPNUMBER = "employmentNumber";
    public static final String EN_WEEKLYHOURS = "weeklyWorkHours";
    public static final String EN_EARNINGS = "earnings";
    public static final String EN_EARNINGSFREQ = "earningsFrequency";
    public static final String EN_OCC = "occupation";
    public static final String EN_OCCODE = "occupationCode";
    public static final String EN_COVERAGES = "coverages";
    public static final String EN_EMAIL = "email";
    public static final String EN_WORKVISA = "workVisaValidUntilDate";
    public static final String EN_MARITAL_STATUS_EFF_DATE = "maritalStatusEffectiveDate";
    public static final String EN_NAME_CHANGE_REASON = "nameChangeReason";
    public static final String EN_NAME_CHANGE_REASON_OTHER = "nameChangeReasonOther";
    public static final String EN_MSP = "mspCoverage";
    public static final String EN_MSP_NUMBER = "provHealthNumber";
    public static final String EN_SMOKER = "smoker";
    
    static
    {
        NOMAD_METHOD_FIELD_LABEL_MAP = new HashMap<>();
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "NameFirst", new NomadModelPropertyInfoEntry( "First Name" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "NameLast", new NomadModelPropertyInfoEntry( "Last Name" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "NameInitial", new NomadModelPropertyInfoEntry( "Middle Initial" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "BirthDate", new NomadModelPropertyInfoEntry( "Date of Birth" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "EmploymentDate", new NomadModelPropertyInfoEntry( "Employment Date" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Sex", new NomadModelPropertyInfoEntry( "Gender" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "MaritalStatus", new NomadModelPropertyInfoEntry( "Marital Status" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Smoker", new NomadModelPropertyInfoEntry( "Smoker" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Earnings", new NomadModelPropertyInfoEntry( "Earnings" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "EarningsFrequency", new NomadModelPropertyInfoEntry( "Earnings Frequency" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "WeeklyWorkHours", new NomadModelPropertyInfoEntry( "Weekly Work Hours" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Occupation", new NomadModelPropertyInfoEntry( "Occupation" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "OccupationClass", new NomadModelPropertyInfoEntry( "Occupation Code", Boolean.FALSE ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "EmailAddress", new NomadModelPropertyInfoEntry( "Email Address" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "EmailAltAddress", new NomadModelPropertyInfoEntry( "Alternate Email Address" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "GroupID", new NomadModelPropertyInfoEntry( "TPA2 GroupID", Boolean.FALSE ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "ClassID", new NomadModelPropertyInfoEntry( "Class" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "ResidenceProvince", new NomadModelPropertyInfoEntry( "Residence Province", Boolean.FALSE));
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "TerminationReasonId", new NomadModelPropertyInfoEntry( "Termination Reason" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "TerminationReason", new NomadModelPropertyInfoEntry( "Termination Reason (Other)" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "TerminationDate", new NomadModelPropertyInfoEntry( "First Day Without Coverage" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "EmploymentTerminationDate", new NomadModelPropertyInfoEntry("Employment Termination Date", Boolean.FALSE));
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "ReinstateDate", new NomadModelPropertyInfoEntry( "Reinstate Date" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "MaritalStatusEffectiveDate", new NomadModelPropertyInfoEntry( "Marital Status Effective Date" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "WorkVisaValidUntilDate", new NomadModelPropertyInfoEntry( "Work Visa Valid Until Date" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "EmploymentNumber", new NomadModelPropertyInfoEntry( "Employment Number" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "DepartmentNumber", new NomadModelPropertyInfoEntry( "Department Number" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "NameChangeReason", new NomadModelPropertyInfoEntry( "Reason for Name Change" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "NameChangeReasonOther", new NomadModelPropertyInfoEntry( "Reason for Name Change (Other)" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "WaiveWaitingPeriodReasonDesc", new NomadModelPropertyInfoEntry( "Reason for Waiving Waiting Period" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "PreferredLanguage", new NomadModelPropertyInfoEntry( "Preferred Language", Boolean.FALSE));
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "MspCoverage", new NomadModelPropertyInfoEntry( "Provincial Coverage" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "ProvHealthNumber", new NomadModelPropertyInfoEntry( "Provincial Health Number" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "ChangeReviewStatusId", new NomadModelPropertyInfoEntry( "Change Review Status Id", Boolean.FALSE ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "SpousalBenefitCoverage", new NomadModelPropertyInfoEntry( "Auto-Coordination", Boolean.FALSE ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "CoupleHealthCoverIsSpousal", new NomadModelPropertyInfoEntry( "Couple Health Spousal Coverage", Boolean.FALSE ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "CoupleDentalCoverIsSpousal", new NomadModelPropertyInfoEntry( "Couple Dental Spousal Coverage", Boolean.FALSE ) );
        /**
         * When adding additional nested values, the NomadWorkflowBO will need to be updated to support the creation and invoking
         * of the parent object.  Always update the NomadWorkflowBOTest when adding new items to be stored
         */
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Phone.AreaCode", new NomadModelPropertyInfoEntry( "Phone Area Code" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Phone.Number", new NomadModelPropertyInfoEntry( "Phone Number" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Phone.Extension", new NomadModelPropertyInfoEntry( "Phone Extension" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "CellPhone.AreaCode", new NomadModelPropertyInfoEntry( "Cell Phone Area Code" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "CellPhone.Number", new NomadModelPropertyInfoEntry( "Cell Phone Number" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "CellPhone.Extension", new NomadModelPropertyInfoEntry( "Cell Extension" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "ContactAddress.Address1", new NomadModelPropertyInfoEntry( "Address 1" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "ContactAddress.Address2", new NomadModelPropertyInfoEntry( "Address 2" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "ContactAddress.City", new NomadModelPropertyInfoEntry( "City" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "ContactAddress.PostCode", new NomadModelPropertyInfoEntry( "Postal Code" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "ContactAddress.Province", new NomadModelPropertyInfoEntry( "Province" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "PartialWaiver.OtherDetails", new NomadModelPropertyInfoEntry( "Partial Waiver - Other Details" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "PartialWaiver.SpousalPlanGroupId", new NomadModelPropertyInfoEntry( "Partial Waiver - Spousal Plan Group ID" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "PartialWaiver.SpousalPlanIdNumber", new NomadModelPropertyInfoEntry( "Partial Waiver - Spousal Plan ID Number" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "PartialWaiver.SupplierDisplayName", new NomadModelPropertyInfoEntry( "Partial Waiver - Supplier Name" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "PartialWaiver.SupplierId", new NomadModelPropertyInfoEntry( "Partial Waiver - Supplier ID", Boolean.FALSE ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "PartialWaiver.OtherSupplier", new NomadModelPropertyInfoEntry( "Partial Waiver - Supplier Name (Other)" ) );
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "PartialWaiver.SpousalPlan", new NomadModelPropertyInfoEntry( "Partial Waiver - Spousal Plan" ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "HealthEnrolment.PolicyNumber", new NomadModelPropertyInfoEntry( "Health Enrolment - Policy Number", Boolean.FALSE ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "HealthEnrolment.UnderwriterId", new NomadModelPropertyInfoEntry( "Health Enrolment - UnderwriterId", Boolean.FALSE ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "HealthEnrolment.EnrolmentId", new NomadModelPropertyInfoEntry( "Health Enrolment - UnderwriterId", Boolean.FALSE ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "HealthEnrolment.EnrollmentDate", new NomadModelPropertyInfoEntry( "Health Enrolment - UnderwriterId", Boolean.FALSE ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "DentalEnrolment.PolicyNumber", new NomadModelPropertyInfoEntry( "Dental Enrolment - Policy Number", Boolean.FALSE ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "DentalEnrolment.UnderwriterId", new NomadModelPropertyInfoEntry( "Dental Enrolment - UnderwriterId", Boolean.FALSE ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "DentalEnrolment.EnrolmentId", new NomadModelPropertyInfoEntry( "Dental Enrolment - EnrolmentId", Boolean.FALSE ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "DentalEnrolment.EnrollmentDate", new NomadModelPropertyInfoEntry( "Dental Enrolment - EnrolmentDate", Boolean.FALSE ) );        
        
        /**
         * When adding additional coverages to be saved in the caching system, @link NomadWorklowBO#updateChangedObject() 
         * will need to be updated to support additional coverage types parent class.  
         */
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Coverages.HealthCoverage.CoverageType", new NomadModelPropertyInfoEntry( "Health Coverage", false ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Coverages.DentalCoverage.CoverageType", new NomadModelPropertyInfoEntry( "Dental Coverage", false ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Coverages.HSACoverage.CoverageType", new NomadModelPropertyInfoEntry( "HSA Coverage", false ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Coverages.PsaCoverage.CoverageType", new NomadModelPropertyInfoEntry( "PSA Coverage", false ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Coverages.DSAICoverage.Status", new NomadModelPropertyInfoEntry( "DSAI Coverage", false ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Coverages.DepLifeCoverage.Covered", new NomadModelPropertyInfoEntry( "Dependent Life Coverage", false ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "Coverages.MSPCoverage.Status", new NomadModelPropertyInfoEntry( "Provincial Health Coverage", false ) );        
        NOMAD_METHOD_FIELD_LABEL_MAP.put( "BenAccount2Int", new NomadModelPropertyInfoEntry( "BenAccount 2.0 Coverage", false ) );        
        PROCESS_FIELD_LIST = new ModelPropertyInfoList();
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("DepartmentNumber", "Department Number", false));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("EmploymentNumber", "Employee Number", false));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("BirthDate", "Date of Birth", true));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("EmploymentDate", "Employment Date", true));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("MaritalStatus", "Marital Status", true, BbdEnums.bbdMaritalStatusEnum.class.getSimpleName()));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("Earnings", "Earnings", true));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("EarningsFrequency", "Earnings Code", true, BbdEnums.earningsFrequencyEnum.class.getSimpleName()));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("WeeklyWorkHours", "Hours Per Week", true));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("Occupation", "Occupation", false));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("OccupationClass", "Occupation Code", true, BbdEnums.bbdOccupationClassEnum.class.getSimpleName()));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("EmailAddress", "Email Address", false));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("ResidenceProvince", "Province", true));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("TerminationReason", "Termination Reason", false));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("DsaiCoverageStatus", "DSAI Status", true, BbdEnums.FamilyCoverageOptionsEnum.class.getSimpleName()));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("Sex", "Sex", true));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("Smoker", "Smoker", true));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("CoupleHealthCoverIsSpousal", "Couple Health Coverage Is Spousal", false));
        PROCESS_FIELD_LIST.add(new ModelPropertyInfoEntry("CoupleDentalCoverIsSpousal", "Couple Dental Coverage Is Spousal", false));

        VALIDATION_REQUIRED_FIELDS_MAP = new HashMap<>();
        VALIDATION_REQUIRED_FIELDS_MAP.put( "NameFirst", new NomadModelPropertyInfoEntry( EN_NAMEFIRST, "First Name" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "NameLast", new NomadModelPropertyInfoEntry( EN_NAMELAST, "Last Name" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "BirthDate", new NomadModelPropertyInfoEntry( EN_BIRTHDATE, "Date of Birth" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "EmploymentDate", new NomadModelPropertyInfoEntry( EN_EMPLOYMENTDATE, "Employment Date" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "Sex", new NomadModelPropertyInfoEntry( EN_SEX, "Sex" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "MaritalStatus", new NomadModelPropertyInfoEntry( EN_NAMEFIRST, "Marital Status" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "Earnings", new NomadModelPropertyInfoEntry( EN_EARNINGS, "Earnings" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "EarningsFrequency", new NomadModelPropertyInfoEntry( EN_EARNINGSFREQ, "Earnings Frequency" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "WeeklyWorkHours", new NomadModelPropertyInfoEntry( EN_WEEKLYHOURS, "Weekly Work Hours" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "Occupation", new NomadModelPropertyInfoEntry( EN_OCC, "Occupation" ) ); 
        VALIDATION_REQUIRED_FIELDS_MAP.put( "OccupationClass", new NomadModelPropertyInfoEntry( EN_OCCCLASS, "Class" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "ContactAddress.Address1", new NomadModelPropertyInfoEntry( EN_ADDRESS, "Address 1" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "ContactAddress.City", new NomadModelPropertyInfoEntry( EN_ADDRESS, "City" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "ContactAddress.PostCode", new NomadModelPropertyInfoEntry( EN_ADDRESS, "Postal Code" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "ContactAddress.Province", new NomadModelPropertyInfoEntry( EN_ADDRESS, "Province" ) );
        VALIDATION_REQUIRED_FIELDS_MAP.put( "GroupID", new NomadModelPropertyInfoEntry( EN_GROUPID, "First Name" ) );        
        VALIDATION_REQUIRED_FIELDS_MAP.put( "ClassID", new NomadModelPropertyInfoEntry( EN_CLASS_ID, "First Name" ) );
    }

}
