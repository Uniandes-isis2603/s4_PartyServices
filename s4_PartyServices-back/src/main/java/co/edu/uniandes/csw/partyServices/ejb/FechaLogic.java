/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.partyServices.ejb;

import co.edu.uniandes.csw.partyServices.entities.AgendaEntity;
import co.edu.uniandes.csw.partyServices.entities.FechaEntity;
import co.edu.uniandes.csw.partyServices.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.partyServices.persistence.AgendaPersistence;
import co.edu.uniandes.csw.partyServices.persistence.FechaPersistence;
import co.edu.uniandes.csw.partyServices.util.ConstantesJornada;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author estudiante
 */
@Stateless
public class FechaLogic {
    
    private static final Logger LOGGER =Logger.getLogger(FechaLogic.class.getName());
    
    @Inject
    private FechaPersistence fechaPersistence;
    
    @Inject
    private AgendaPersistence agendaPersistence;
    
    public FechaEntity createFecha(long agendaId, FechaEntity fechaEntity) throws BusinessLogicException
    {
        //Verificacion regla de negocio de las jornadas
        if(ConstantesJornada.desdeValor(fechaEntity.getJornada()) == null){
                throw new BusinessLogicException("No cumple con las jornadas posibles");          
        }
        if(ConstantesJornada.desdeValor( fechaEntity.getJornada() ).equals(ConstantesJornada.NINGUNA)){          
            throw new BusinessLogicException("La jornada ninguna no es valida");
        } 
        
        if(fechaEntity.getDia()==null)
            throw new BusinessLogicException("La fecha es nula");
            
        
        //Verificacion regla de negocio no se pueden crear fechas del pasado
        Date dia= new Date();
        if(dia.compareTo(fechaEntity.getDia())>=0)
            throw new BusinessLogicException("El dia de la fecha no puede ser menor o igual al dia actual");
        
        AgendaEntity agenda = agendaPersistence.find(agendaId);
        if(agenda==null)
            throw new BusinessLogicException("La agenda de la fecha que esta creando no existe");
        fechaEntity.setAgenda(agenda);
        
        Collection<FechaEntity>fechasOcupadas=agenda.getFechasOcupadas();
        if(fechasOcupadas==null)
            fechasOcupadas=new ArrayList<>();
        fechasOcupadas.add(fechaEntity);
        agenda.setFechasOcupadas(fechasOcupadas);
        agendaPersistence.update(agenda);
        return fechaPersistence.create(fechaEntity);
    }
    
    public FechaEntity getFechaPorDiaAgendaJornada(Date dia, long idAgenda, String jornada) throws BusinessLogicException
    {
        LOGGER.log(Level.INFO,"Entrando a optener fecha {0}", dia);
        FechaEntity fecha= fechaPersistence.findByDiaAgendaAndJornada(dia, idAgenda, jornada);
        if(fecha==null){
            LOGGER.log(Level.INFO,"No se encuentra fecha con el id {0}", dia);
            throw new BusinessLogicException("No existe fecha con dia, idAgenda y jornada "+dia.toString()+idAgenda+jornada);
        }
        return fecha;
    }
    
    public FechaEntity getFechaID(long idFecha) throws BusinessLogicException
    {
        LOGGER.log(Level.INFO,"Entrando a optener fecha {0}", idFecha);
        FechaEntity fecha= fechaPersistence.find(idFecha);
        if(fecha==null){
            LOGGER.log(Level.INFO,"No se encuentra fecha con el id {0}", idFecha);
            throw new BusinessLogicException("No existe fecha con id "+idFecha);
        }
        return fecha;
    }
    
    public FechaEntity updateFecha(FechaEntity fechaEntity) throws BusinessLogicException
    {
        //Verificacion regla de negocio de las jornadas
        if(ConstantesJornada.desdeValor(fechaEntity.getJornada()) == null){
                throw new BusinessLogicException("No cumple con las jornadas posibles al actualizar la fecha");          
        }
        if(ConstantesJornada.desdeValor(fechaEntity.getJornada()).darValor().equals(ConstantesJornada.NINGUNA.darValor())){
                throw new BusinessLogicException("No es valido a la jornada ninguna actualizando la fecha");          
        }
        return fechaPersistence.update(fechaEntity);
    }
    
    public void deleteFecha(long fechaId) throws BusinessLogicException
    {
        FechaEntity fecha =fechaPersistence.find(fechaId);
        if(fecha!=null && fecha.getEventos()!=null && !fecha.getEventos().isEmpty())
            throw new BusinessLogicException("No se puede eliminar la fecha porque tiene "+fecha.getEventos().size()+" eventos");
            
        fechaPersistence.delete(fechaId);
    }
    
}
